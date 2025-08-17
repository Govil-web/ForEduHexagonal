package com.academia.domain.model.entities;

import com.academia.domain.model.enums.AuditEventType;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Entidad de dominio para logs de auditoría de seguridad.
 * Registra eventos críticos para compliance y forense.
 */
@Getter
public class AuditLog {
    
    private final Long id;
    private final AuditEventType eventType;
    private final AccountId userId;
    private final OrganizationId organizationId;
    private final String userEmail;
    private final String ipAddress;
    private final String userAgent;
    private final String resource;
    private final String action;
    private final boolean success;
    private final String failureReason;
    private final Map<String, Object> metadata;
    private final LocalDateTime timestamp;
    private final String sessionId;
    private final String requestId;
    
    public AuditLog(
            Long id,
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
            Map<String, Object> metadata,
            String sessionId,
            String requestId
    ) {
        this.id = id;
        this.eventType = eventType;
        this.userId = userId;
        this.organizationId = organizationId;
        this.userEmail = userEmail;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.resource = resource;
        this.action = action;
        this.success = success;
        this.failureReason = failureReason;
        this.metadata = metadata;
        this.timestamp = LocalDateTime.now();
        this.sessionId = sessionId;
        this.requestId = requestId;
    }
    
    /**
     * Constructor completo incluyendo timestamp (para mappers de persistencia).
     */
    public AuditLog(
            Long id,
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
            Map<String, Object> metadata,
            String sessionId,
            String requestId,
            LocalDateTime timestamp
    ) {
        this.id = id;
        this.eventType = eventType;
        this.userId = userId;
        this.organizationId = organizationId;
        this.userEmail = userEmail;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.resource = resource;
        this.action = action;
        this.success = success;
        this.failureReason = failureReason;
        this.metadata = metadata;
        this.timestamp = timestamp;
        this.sessionId = sessionId;
        this.requestId = requestId;
    }
    
    /**
     * Crea un log de auditoría para login exitoso.
     */
    public static AuditLog loginSuccess(
            AccountId userId,
            OrganizationId organizationId, 
            String userEmail,
            String ipAddress,
            String userAgent,
            String sessionId
    ) {
        return new AuditLog(
            null, AuditEventType.LOGIN_SUCCESS, userId, organizationId,
            userEmail, ipAddress, userAgent, "authentication", "login",
            true, null, Map.of("loginMethod", "credentials"),
            sessionId, null
        );
    }
    
    /**
     * Crea un log de auditoría para login fallido.
     */
    public static AuditLog loginFailed(
            String userEmail,
            String ipAddress,
            String userAgent,
            String failureReason
    ) {
        return new AuditLog(
            null, AuditEventType.LOGIN_FAILED, null, null,
            userEmail, ipAddress, userAgent, "authentication", "login",
            false, failureReason, Map.of("attemptedEmail", userEmail),
            null, null
        );
    }
    
    /**
     * Crea un log de auditoría para acceso denegado.
     */
    public static AuditLog accessDenied(
            AccountId userId,
            OrganizationId organizationId,
            String userEmail,
            String ipAddress,
            String resource,
            String action,
            String reason
    ) {
        return new AuditLog(
            null, AuditEventType.ACCESS_DENIED, userId, organizationId,
            userEmail, ipAddress, null, resource, action,
            false, reason, Map.of("deniedResource", resource),
            null, null
        );
    }
    
    /**
     * Verifica si este evento es considerado de alta criticidad.
     */
    public boolean isHighCriticality() {
        return switch (eventType) {
            case LOGIN_BLOCKED, PERMISSION_ESCALATION_ATTEMPT, SUSPICIOUS_ACTIVITY_DETECTED,
                 BRUTE_FORCE_ATTEMPT, SECURITY_POLICY_VIOLATION, API_UNAUTHORIZED_ACCESS -> true;
            default -> false;
        };
    }
    
    /**
     * Verifica si este evento requiere notificación inmediata.
     */
    public boolean requiresImmediateNotification() {
        return switch (eventType) {
            case SUSPICIOUS_ACTIVITY_DETECTED, PERMISSION_ESCALATION_ATTEMPT,
                 SECURITY_POLICY_VIOLATION, SYSTEM_CONFIGURATION_CHANGED -> true;
            default -> false;
        };
    }
}