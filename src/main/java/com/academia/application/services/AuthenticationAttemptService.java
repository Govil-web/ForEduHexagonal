package com.academia.application.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Servicio para gestionar y limitar intentos de autenticación.
 * Implementa rate limiting basado en IP y email.
 */
@Service
@Slf4j
public class AuthenticationAttemptService {

    private final int maxAttempts;
    private final int attemptWindowMinutes;

    // Cache en memoria para demo - en producción usar Redis
    private final ConcurrentMap<String, AttemptInfo> attemptCache = new ConcurrentHashMap<>();

    public AuthenticationAttemptService(
            @Value("${app.auth.max-login-attempts:5}") int maxAttempts,
            @Value("${app.auth.login-attempt-window-minutes:1}") int attemptWindowMinutes
    ) {
        this.maxAttempts = maxAttempts;
        this.attemptWindowMinutes = attemptWindowMinutes;
    }

    /**
     * Registra un intento de login fallido.
     *
     * @param identifier Identificador único (IP + email)
     */
    public void recordFailedAttempt(String identifier) {
        log.debug("Registrando intento fallido para: {}", identifier);

        AttemptInfo info = attemptCache.computeIfAbsent(identifier, k -> new AttemptInfo());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.minusMinutes(attemptWindowMinutes);

        // Limpiar intentos fuera de la ventana de tiempo
        info.attempts.removeIf(attempt -> attempt.isBefore(windowStart));

        // Agregar el nuevo intento
        info.attempts.add(now);

        log.warn("Usuario {} tiene {} intentos fallidos en {} minutos",
                identifier, info.attempts.size(), attemptWindowMinutes);
    }

    /**
     * Verifica si un identificador está bloqueado por exceder intentos.
     *
     * @param identifier Identificador único (IP + email)
     * @return true si está bloqueado
     */
    public boolean isBlocked(String identifier) {
        AttemptInfo info = attemptCache.get(identifier);
        if (info == null) {
            return false;
        }

        LocalDateTime windowStart = LocalDateTime.now().minusMinutes(attemptWindowMinutes);

        // Limpiar intentos antiguos
        info.attempts.removeIf(attempt -> attempt.isBefore(windowStart));

        boolean blocked = info.attempts.size() >= maxAttempts;

        if (blocked) {
            log.warn("Acceso bloqueado para {} - {} intentos en {} minutos",
                    identifier, info.attempts.size(), attemptWindowMinutes);
        }

        return blocked;
    }

    /**
     * Limpia los intentos fallidos de un identificador (tras login exitoso).
     *
     * @param identifier Identificador único
     */
    public void clearFailedAttempts(String identifier) {
        log.debug("Limpiando intentos fallidos para: {}", identifier);
        attemptCache.remove(identifier);
    }

    /**
     * Obtiene el número de intentos restantes antes del bloqueo.
     *
     * @param identifier Identificador único
     * @return Número de intentos restantes
     */
    public int getRemainingAttempts(String identifier) {
        AttemptInfo info = attemptCache.get(identifier);
        if (info == null) {
            return maxAttempts;
        }

        LocalDateTime windowStart = LocalDateTime.now().minusMinutes(attemptWindowMinutes);
        info.attempts.removeIf(attempt -> attempt.isBefore(windowStart));

        return Math.max(0, maxAttempts - info.attempts.size());
    }

    /**
     * Obtiene información sobre el tiempo restante de bloqueo.
     *
     * @param identifier Identificador único
     * @return Minutos hasta que se libere el bloqueo
     */
    public long getBlockTimeRemainingMinutes(String identifier) {
        if (!isBlocked(identifier)) {
            return 0;
        }

        AttemptInfo info = attemptCache.get(identifier);
        if (info == null || info.attempts.isEmpty()) {
            return 0;
        }

        LocalDateTime oldestAttempt = info.attempts.stream()
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        LocalDateTime unblockTime = oldestAttempt.plusMinutes(attemptWindowMinutes);

        return Math.max(0, java.time.Duration.between(LocalDateTime.now(), unblockTime).toMinutes());
    }

    /**
     * Clase interna para almacenar información de intentos.
     */
    private static class AttemptInfo {
        final java.util.List<LocalDateTime> attempts = new java.util.concurrent.CopyOnWriteArrayList<>();
    }
}