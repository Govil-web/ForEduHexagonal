package com.academia.application.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Servicio de rate limiting distribuido usando Redis.
 * Implementa algoritmo de sliding window para limitar peticiones por cliente.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisRateLimitService {

    private final StringRedisTemplate redisTemplate;

    @Value("${app.security.rate-limiting.enabled:true}")
    private boolean rateLimitingEnabled;

    @Value("${app.security.rate-limiting.requests-per-minute:60}")
    private int requestsPerMinute;

    @Value("${app.security.rate-limiting.burst-size:20}")
    private int burstSize;

    // Script Lua para operación atómica de rate limiting
    private static final String RATE_LIMIT_SCRIPT = """
        local key = KEYS[1]
        local window = tonumber(ARGV[1])
        local limit = tonumber(ARGV[2])
        local current_time = tonumber(ARGV[3])
        
        -- Limpiar entradas antiguas
        redis.call('ZREMRANGEBYSCORE', key, 0, current_time - window)
        
        -- Contar entradas actuales
        local current_count = redis.call('ZCARD', key)
        
        if current_count < limit then
            -- Agregar nueva entrada
            redis.call('ZADD', key, current_time, current_time)
            redis.call('EXPIRE', key, window)
            return {1, limit - current_count - 1}
        else
            return {0, 0}
        end
        """;

    private final DefaultRedisScript<List> rateLimitScript = new DefaultRedisScript<>(RATE_LIMIT_SCRIPT, List.class);

    /**
     * Verifica si una petición está permitida según las reglas de rate limiting.
     *
     * @param clientId Identificador del cliente (IP, usuario, etc.)
     * @param endpoint Endpoint específico (opcional para límites granulares)
     * @return RateLimitResult con el resultado de la verificación
     */
    public RateLimitResult isRequestAllowed(String clientId, String endpoint) {
        if (!rateLimitingEnabled) {
            return RateLimitResult.allowed(requestsPerMinute);
        }

        try {
            String key = buildRateLimitKey(clientId, endpoint);
            long currentTimeMillis = System.currentTimeMillis();
            long windowSizeMillis = Duration.ofMinutes(1).toMillis();

            List result = redisTemplate.execute(
                rateLimitScript,
                List.of(key),
                String.valueOf(windowSizeMillis),
                String.valueOf(requestsPerMinute),
                String.valueOf(currentTimeMillis)
            );

            boolean allowed = ((Long) result.get(0)) == 1;
            int remaining = ((Long) result.get(1)).intValue();

            if (allowed) {
                log.debug("Request allowed for client: {}, remaining: {}", clientId, remaining);
                return RateLimitResult.allowed(remaining);
            } else {
                log.warn("Request rate limited for client: {}", clientId);
                
                // Obtener tiempo hasta reset
                long resetTime = getResetTime(key);
                return RateLimitResult.rateLimited(0, resetTime);
            }

        } catch (Exception e) {
            log.error("Error en rate limiting para cliente {}: {}", clientId, e.getMessage());
            // En caso de error, permitir la petición (fail-open)
            return RateLimitResult.allowed(requestsPerMinute);
        }
    }

    /**
     * Verifica rate limiting específico para login (más estricto).
     */
    public RateLimitResult isLoginAllowed(String clientId, String email) {
        String loginKey = "login:" + clientId + ":" + email;
        return isRequestAllowedWithCustomLimit(loginKey, 5, Duration.ofMinutes(15));
    }

    /**
     * Verifica rate limiting para APIs (más permisivo).
     */
    public RateLimitResult isApiRequestAllowed(String apiKey, String endpoint) {
        String apiLimitKey = "api:" + apiKey + ":" + endpoint;
        return isRequestAllowedWithCustomLimit(apiLimitKey, 1000, Duration.ofHours(1));
    }

    /**
     * Implementa burst protection - límite muy corto para prevenir ataques rápidos.
     */
    public RateLimitResult isBurstAllowed(String clientId) {
        String burstKey = "burst:" + clientId;
        return isRequestAllowedWithCustomLimit(burstKey, burstSize, Duration.ofSeconds(10));
    }

    /**
     * Rate limiting con límites personalizados.
     */
    private RateLimitResult isRequestAllowedWithCustomLimit(String key, int limit, Duration window) {
        try {
            long currentTimeMillis = System.currentTimeMillis();
            long windowSizeMillis = window.toMillis();

            List result = redisTemplate.execute(
                rateLimitScript,
                List.of(key),
                String.valueOf(windowSizeMillis),
                String.valueOf(limit),
                String.valueOf(currentTimeMillis)
            );

            boolean allowed = ((Long) result.get(0)) == 1;
            int remaining = ((Long) result.get(1)).intValue();

            if (!allowed) {
                long resetTime = getResetTime(key);
                return RateLimitResult.rateLimited(0, resetTime);
            }

            return RateLimitResult.allowed(remaining);

        } catch (Exception e) {
            log.error("Error en rate limiting personalizado para {}: {}", key, e.getMessage());
            return RateLimitResult.allowed(limit);
        }
    }

    /**
     * Construye la clave Redis para rate limiting.
     */
    private String buildRateLimitKey(String clientId, String endpoint) {
        if (endpoint != null && !endpoint.isEmpty()) {
            return String.format("rate_limit:%s:%s", clientId, endpoint);
        }
        return String.format("rate_limit:%s", clientId);
    }

    /**
     * Obtiene el tiempo de reset de la ventana de rate limiting.
     */
    private long getResetTime(String key) {
        try {
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            if (ttl != null && ttl > 0) {
                return System.currentTimeMillis() + (ttl * 1000);
            }
        } catch (Exception e) {
            log.debug("Error obteniendo TTL para {}: {}", key, e.getMessage());
        }
        return System.currentTimeMillis() + Duration.ofMinutes(1).toMillis();
    }

    /**
     * Limpia entradas de rate limiting para un cliente (admin override).
     */
    public void clearRateLimit(String clientId) {
        try {
            String pattern = "rate_limit:" + clientId + "*";
            var keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("Rate limit cleared for client: {}", clientId);
            }
        } catch (Exception e) {
            log.error("Error clearing rate limit for {}: {}", clientId, e.getMessage());
        }
    }

    /**
     * Obtiene estadísticas de rate limiting para monitoreo.
     */
    public RateLimitStats getStats(String clientId) {
        try {
            String key = buildRateLimitKey(clientId, null);
            Long count = redisTemplate.opsForZSet().count(key, 
                System.currentTimeMillis() - Duration.ofMinutes(1).toMillis(), 
                System.currentTimeMillis());
            
            return new RateLimitStats(
                clientId,
                count != null ? count.intValue() : 0,
                requestsPerMinute,
                getResetTime(key)
            );
        } catch (Exception e) {
            log.error("Error getting stats for {}: {}", clientId, e.getMessage());
            return new RateLimitStats(clientId, 0, requestsPerMinute, 
                System.currentTimeMillis() + Duration.ofMinutes(1).toMillis());
        }
    }

    /**
     * Resultado de la verificación de rate limiting.
     */
    public static class RateLimitResult {
        private final boolean allowed;
        private final int remainingRequests;
        private final long resetTimeMillis;

        private RateLimitResult(boolean allowed, int remainingRequests, long resetTimeMillis) {
            this.allowed = allowed;
            this.remainingRequests = remainingRequests;
            this.resetTimeMillis = resetTimeMillis;
        }

        public static RateLimitResult allowed(int remaining) {
            return new RateLimitResult(true, remaining, 0);
        }

        public static RateLimitResult rateLimited(int remaining, long resetTime) {
            return new RateLimitResult(false, remaining, resetTime);
        }

        public boolean isAllowed() { return allowed; }
        public int getRemainingRequests() { return remainingRequests; }
        public long getResetTimeMillis() { return resetTimeMillis; }
        public Duration getResetTimeRemaining() {
            return Duration.ofMillis(Math.max(0, resetTimeMillis - System.currentTimeMillis()));
        }
    }

    /**
     * Estadísticas de rate limiting para monitoreo.
     */
    public static class RateLimitStats {
        private final String clientId;
        private final int currentRequests;
        private final int maxRequests;
        private final long resetTimeMillis;

        public RateLimitStats(String clientId, int currentRequests, int maxRequests, long resetTimeMillis) {
            this.clientId = clientId;
            this.currentRequests = currentRequests;
            this.maxRequests = maxRequests;
            this.resetTimeMillis = resetTimeMillis;
        }

        public String getClientId() { return clientId; }
        public int getCurrentRequests() { return currentRequests; }
        public int getMaxRequests() { return maxRequests; }
        public long getResetTimeMillis() { return resetTimeMillis; }
        public double getUsagePercentage() { return (double) currentRequests / maxRequests * 100; }
    }
}