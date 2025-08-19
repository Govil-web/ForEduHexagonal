package com.academia.application.services;

import com.academia.domain.model.entities.AuditLog;
import com.academia.domain.model.enums.AuditEventType;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.ports.out.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Servicio de aplicación para gestionar auditoría de seguridad.
 * Centraliza el logging de eventos críticos de seguridad.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityAuditService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Registra un evento de auditoría de seguridad.
     */
    public void logSecurityEvent(
            AuditEventType eventType,
            AccountId userId,
            OrganizationId organizationId,
            String userEmail,
            String ipAddress,
            String userAgent,
            String resource,
            String action,
            boolean success,
            String failureReason,
            Map<String, Object> metadata
    ) {
        try {
            AuditLog auditLog = new AuditLog(
                null, eventType, userId, organizationId, userEmail,
                ipAddress, userAgent, resource, action, success,
                failureReason, metadata, extractSessionId(), extractRequestId()
            );

            // Guardar en base de datos
            auditLogRepository.save(auditLog);

            // Log estructurado para SIEM/observabilidad
            if (auditLog.isHighCriticality()) {
                log.warn("SECURITY_CRITICAL_EVENT: type={}, user={}, ip={}, resource={}, success={}, reason={}",
                    eventType, userEmail, ipAddress, resource, success, failureReason);
            } else {
                log.info("SECURITY_EVENT: type={}, user={}, ip={}, resource={}, success={}",
                    eventType, userEmail, ipAddress, resource, success);
            }

            // Notificación inmediata para eventos críticos
            if (auditLog.requiresImmediateNotification()) {
                sendImmediateAlert(auditLog);
            }

        } catch (Exception e) {
            // Nunca fallar por problemas de auditoría, pero logear el error
            log.error("Error registrando evento de auditoría: {}", e.getMessage(), e);
        }
    }

    /**
     * Registra login exitoso.
     */
    public void logLoginSuccess(
            AccountId userId,
            OrganizationId organizationId,
            String userEmail,
            String ipAddress,
            String userAgent
    ) {
        logSecurityEvent(
            AuditEventType.LOGIN_SUCCESS, userId, organizationId, userEmail,
            ipAddress, userAgent, "authentication", "login", true, null,
            Map.of("loginMethod", "credentials", "timestamp", LocalDateTime.now())
        );
    }

    /**
     * Registra login fallido.
     */
    public void logLoginFailure(
            String userEmail,
            String ipAddress,
            String userAgent,
            String failureReason
    ) {
        logSecurityEvent(
            AuditEventType.LOGIN_FAILED, null, null, userEmail,
            ipAddress, userAgent, "authentication", "login", false, failureReason,
            Map.of("attemptedEmail", userEmail, "timestamp", LocalDateTime.now())
        );
    }

    /**
     * Registra intento de escalación de privilegios.
     */
    public void logPrivilegeEscalationAttempt(
            AccountId userId,
            OrganizationId organizationId,
            String userEmail,
            String ipAddress,
            String attemptedResource,
            String requiredPermission
    ) {
        logSecurityEvent(
            AuditEventType.PERMISSION_ESCALATION_ATTEMPT, userId, organizationId, userEmail,
            ipAddress, null, attemptedResource, "access", false, "Insufficient privileges",
            Map.of(
                "requiredPermission", requiredPermission,
                "attemptedResource", attemptedResource,
                "severity", "HIGH"
            )
        );
    }

    /**
     * Registra acceso denegado.
     */
    public void logAccessDenied(
            AccountId userId,
            OrganizationId organizationId,
            String userEmail,
            String ipAddress,
            String resource,
            String action,
            String reason
    ) {
        logSecurityEvent(
            AuditEventType.ACCESS_DENIED, userId, organizationId, userEmail,
            ipAddress, null, resource, action, false, reason,
            Map.of("deniedReason", reason)
        );
    }

    /**
     * Registra un evento de negocio genérico para auditoría.
     */
    public void logBusinessEvent(
            String action,
            String resource,
            String description,
            OrganizationId organizationId,
            Map<String, Object> metadata
    ) {
        // Aquí asumimos que el evento es exitoso y no tenemos un contexto de usuario específico (IP, etc.)
        // que es común para eventos de sistema o asíncronos.
        logSecurityEvent(
                AuditEventType.BUSINESS_PROCESS, // Un tipo genérico para eventos de negocio
                null, // userId (no disponible en este contexto)
                organizationId,
                "system", // userEmail (marcado como sistema)
                "N/A", // ipAddress
                "N/A", // userAgent
                resource,
                action,
                true, // success
                null, // failureReason
                metadata
        );
    }

    /**
     * Registra un evento genérico simplificado.
     */
    public void logEvent(String eventType, String description, String organizationIdStr) {
        try {
            OrganizationId orgId = organizationIdStr != null ? OrganizationId.of(Long.valueOf(organizationIdStr)) : null;
            
            logSecurityEvent(
                AuditEventType.BUSINESS_PROCESS,
                null, // userId
                orgId, // organizationId
                "system", // userEmail
                "N/A", // ipAddress
                "N/A", // userAgent
                eventType, // resource
                "system_event", // action
                true, // success
                null, // failureReason
                Map.of("description", description, "eventType", eventType)
            );
        } catch (Exception e) {
            log.error("Error logging event: {}", e.getMessage(), e);
        }
    }

    /**
     * Registra actividad sospechosa.
     */
    public void logSuspiciousActivity(
            String ipAddress,
            String userEmail,
            String activityType,
            String details
    ) {
        logSecurityEvent(
            AuditEventType.SUSPICIOUS_ACTIVITY_DETECTED, null, null, userEmail,
            ipAddress, null, "system", activityType, false, details,
            Map.of(
                "activityType", activityType,
                "details", details,
                "severity", "HIGH",
                "requiresInvestigation", true
            )
        );
    }

    /**
     * Analiza patrones sospechosos en los logs recientes.
     */
    public void analyzeSecurityPatterns() {
        LocalDateTime since = LocalDateTime.now().minusHours(1);

        // Detectar intentos de fuerza bruta
        detectBruteForceAttempts(since);

        // Detectar accesos desde IPs sospechosas
        detectSuspiciousIPs(since);

        // Detectar intentos de escalación de privilegios
        detectPrivilegeEscalationPatterns(since);
    }

    private void detectBruteForceAttempts(LocalDateTime since) {
        List<AuditLog> failedLogins = auditLogRepository.findByEventType(AuditEventType.LOGIN_FAILED, since);
        
        // Agrupar por IP y email, detectar patrones
        Map<String, Long> failuresByIp = failedLogins.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                AuditLog::getIpAddress,
                java.util.stream.Collectors.counting()
            ));

        failuresByIp.entrySet().stream()
            .filter(entry -> entry.getValue() >= 10) // 10+ fallos en 1 hora
            .forEach(entry -> {
                logSuspiciousActivity(
                    entry.getKey(),
                    "unknown",
                    "BRUTE_FORCE_ATTEMPT",
                    String.format("Detectados %d intentos de login fallidos desde IP %s", 
                        entry.getValue(), entry.getKey())
                );
            });
    }

    private void detectSuspiciousIPs(LocalDateTime since) {
        // Implementar detección de IPs con comportamiento anómalo
        List<AuditLog> recentEvents = auditLogRepository.findHighCriticalityEvents(since);
        
        // Detectar IPs con múltiples eventos críticos
        Map<String, Long> criticalEventsByIp = recentEvents.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                AuditLog::getIpAddress,
                java.util.stream.Collectors.counting()
            ));

        criticalEventsByIp.entrySet().stream()
            .filter(entry -> entry.getValue() >= 5)
            .forEach(entry -> {
                logSuspiciousActivity(
                    entry.getKey(),
                    "unknown",
                    "SUSPICIOUS_IP_ACTIVITY",
                    String.format("IP %s generó %d eventos críticos", entry.getKey(), entry.getValue())
                );
            });
    }

    private void detectPrivilegeEscalationPatterns(LocalDateTime since) {
        List<AuditLog> escalationAttempts = auditLogRepository.findByEventType(
            AuditEventType.PERMISSION_ESCALATION_ATTEMPT, since
        );

        // Detectar usuarios con múltiples intentos de escalación
        Map<String, Long> escalationsByUser = escalationAttempts.stream()
            .filter(log -> log.getUserEmail() != null)
            .collect(java.util.stream.Collectors.groupingBy(
                AuditLog::getUserEmail,
                java.util.stream.Collectors.counting()
            ));

        escalationsByUser.entrySet().stream()
            .filter(entry -> entry.getValue() >= 3)
            .forEach(entry -> {
                logSuspiciousActivity(
                    "unknown",
                    entry.getKey(),
                    "PRIVILEGE_ESCALATION_PATTERN",
                    String.format("Usuario %s intentó escalación de privilegios %d veces", 
                        entry.getKey(), entry.getValue())
                );
            });
    }

    private void sendImmediateAlert(AuditLog auditLog) {
        // Implementar notificación inmediata (email, Slack, webhook, etc.)
        log.error("IMMEDIATE_SECURITY_ALERT: {} - User: {}, IP: {}, Resource: {}",
            auditLog.getEventType(), auditLog.getUserEmail(), 
            auditLog.getIpAddress(), auditLog.getResource());
        
        // TODO: Integrar con sistema de alertas (PagerDuty, Slack, etc.)
    }

    private String extractSessionId() {
        // Extraer session ID del contexto de Spring Security si está disponible
        return "session-" + System.currentTimeMillis();
    }

    private String extractRequestId() {
        // Extraer request ID del MDC o generar uno nuevo
        return "req-" + String.valueOf(System.currentTimeMillis()).substring(5);
    }
}