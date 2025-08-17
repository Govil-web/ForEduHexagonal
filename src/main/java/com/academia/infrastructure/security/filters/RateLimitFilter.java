package com.academia.infrastructure.security.filters;

import com.academia.application.services.RedisRateLimitService;
import com.academia.application.services.SecurityAuditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

/**
 * Filtro de rate limiting que protege la aplicación contra abuso y ataques DoS.
 * Implementa múltiples niveles de protección con Redis distribuido.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private final RedisRateLimitService rateLimitService;
    private final SecurityAuditService securityAuditService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String clientId = extractClientId(request);
        String endpoint = getEndpointIdentifier(request);
        String userAgent = request.getHeader("User-Agent");

        try {
            // 1. Verificar burst protection (muy corto plazo)
            RedisRateLimitService.RateLimitResult burstResult = rateLimitService.isBurstAllowed(clientId);
            if (!burstResult.isAllowed()) {
                handleRateLimit(request, response, burstResult, "BURST_LIMIT_EXCEEDED", clientId);
                return;
            }

            // 2. Verificar rate limit general
            RedisRateLimitService.RateLimitResult generalResult = rateLimitService.isRequestAllowed(clientId, endpoint);
            if (!generalResult.isAllowed()) {
                handleRateLimit(request, response, generalResult, "RATE_LIMIT_EXCEEDED", clientId);
                return;
            }

            // 3. Rate limiting específico para login (más estricto)
            if (isLoginEndpoint(request)) {
                String email = extractEmailFromRequest(request);
                RedisRateLimitService.RateLimitResult loginResult = rateLimitService.isLoginAllowed(clientId, email);
                if (!loginResult.isAllowed()) {
                    handleRateLimit(request, response, loginResult, "LOGIN_RATE_LIMIT_EXCEEDED", clientId);
                    return;
                }
            }

            // Agregar headers informativos de rate limiting
            addRateLimitHeaders(response, generalResult);

            // Continuar con la cadena de filtros
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("Error en rate limiting filter para cliente {}: {}", clientId, e.getMessage());
            // En caso de error, permitir la petición (fail-open)
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Extrae el identificador del cliente (IP + User-Agent hash).
     */
    private String extractClientId(HttpServletRequest request) {
        String clientIP = getClientIP(request);
        String userAgent = request.getHeader("User-Agent");
        
        // Combinar IP con hash del User-Agent para mejor granularidad
        if (userAgent != null) {
            int userAgentHash = userAgent.hashCode();
            return clientIP + ":" + userAgentHash;
        }
        
        return clientIP;
    }

    /**
     * Obtiene la IP real del cliente considerando proxies y load balancers.
     */
    private String getClientIP(HttpServletRequest request) {
        // Headers comunes de proxies/load balancers
        String[] headerNames = {
            "X-Forwarded-For",
            "X-Real-IP", 
            "X-Originating-IP",
            "CF-Connecting-IP", // Cloudflare
            "True-Client-IP"
        };

        for (String headerName : headerNames) {
            String ip = request.getHeader(headerName);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For puede contener múltiples IPs separadas por coma
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }

        return request.getRemoteAddr();
    }

    /**
     * Identifica el endpoint para rate limiting granular.
     */
    private String getEndpointIdentifier(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // Normalizar paths dinámicos
        if (path.contains("/api/v1/")) {
            // Simplificar paths con IDs dinámicos
            path = path.replaceAll("/\\d+", "/{id}");
            path = path.replaceAll("/[a-fA-F0-9-]{36}", "/{uuid}");
        }
        
        return method + ":" + path;
    }

    /**
     * Verifica si es un endpoint de login.
     */
    private boolean isLoginEndpoint(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        return "POST".equalsIgnoreCase(method) && 
               (path.contains("/auth/login") || path.contains("/login"));
    }

    /**
     * Extrae el email del cuerpo de la petición de login.
     */
    private String extractEmailFromRequest(HttpServletRequest request) {
        // Para simplicidad, usar un placeholder
        // En implementación real, se debería leer del cuerpo de la request
        return "unknown";
    }

    /**
     * Maneja el caso de rate limit excedido.
     */
    private void handleRateLimit(
            HttpServletRequest request,
            HttpServletResponse response,
            RedisRateLimitService.RateLimitResult result,
            String reason,
            String clientId
    ) throws IOException {
        
        // Auditar el evento de rate limiting
        securityAuditService.logSuspiciousActivity(
            getClientIP(request),
            "unknown",
            reason,
            String.format("Rate limit exceeded for client %s on endpoint %s", 
                clientId, getEndpointIdentifier(request))
        );

        // Configurar respuesta HTTP
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        // Headers estándar de rate limiting
        addRateLimitHeaders(response, result);
        response.setHeader("Retry-After", String.valueOf(result.getResetTimeRemaining().getSeconds()));

        // Cuerpo de respuesta JSON
        Map<String, Object> errorResponse = Map.of(
            "error", "RATE_LIMIT_EXCEEDED",
            "message", "Too many requests. Please try again later.",
            "details", Map.of(
                "limit", "Check X-RateLimit-Limit header",
                "remaining", result.getRemainingRequests(),
                "resetTime", result.getResetTimeMillis(),
                "retryAfter", result.getResetTimeRemaining().getSeconds()
            )
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.getWriter().flush();

        log.warn("Rate limit exceeded for client {} on endpoint {}: {}", 
            clientId, getEndpointIdentifier(request), reason);
    }

    /**
     * Agrega headers informativos de rate limiting.
     */
    private void addRateLimitHeaders(HttpServletResponse response, RedisRateLimitService.RateLimitResult result) {
        response.setHeader("X-RateLimit-Remaining", String.valueOf(result.getRemainingRequests()));
        
        if (result.getResetTimeMillis() > 0) {
            response.setHeader("X-RateLimit-Reset", String.valueOf(result.getResetTimeMillis() / 1000));
        }
        
        // Headers adicionales para debugging (solo en desarrollo)
        response.setHeader("X-RateLimit-Policy", "sliding-window");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        
        // No aplicar rate limiting a endpoints de salud y recursos estáticos
        return path.startsWith("/actuator/health") ||
               path.startsWith("/favicon.ico") ||
               path.startsWith("/static/") ||
               path.startsWith("/public/");
    }
}