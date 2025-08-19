package com.academia.domain.model.enums;

/**
 * Tipos de eventos de auditoría para el sistema de seguridad.
 */
public enum AuditEventType {
    // Eventos de Autenticación
    LOGIN_SUCCESS,
    LOGIN_FAILED,
    LOGIN_BLOCKED,
    LOGOUT,
    TOKEN_REFRESH,
    TOKEN_EXPIRED,
    
    // Eventos de Autorización
    ACCESS_GRANTED,
    ACCESS_DENIED,
    PERMISSION_ESCALATION_ATTEMPT,
    
    // Eventos de Sesión
    SESSION_CREATED,
    SESSION_EXPIRED,
    SESSION_INVALIDATED,
    CONCURRENT_SESSION_DETECTED,
    
    // Eventos de Cuenta
    ACCOUNT_CREATED,
    ACCOUNT_MODIFIED,
    ACCOUNT_SUSPENDED,
    ACCOUNT_REACTIVATED,
    PASSWORD_CHANGED,
    PASSWORD_RESET_REQUESTED,
    PASSWORD_RESET_COMPLETED,
    
    // Eventos de Roles y Permisos
    ROLE_ASSIGNED,
    ROLE_REMOVED,
    PERMISSION_GRANTED,
    PERMISSION_REVOKED,
    
    // Eventos de Seguridad
    SUSPICIOUS_ACTIVITY_DETECTED,
    BRUTE_FORCE_ATTEMPT,
    RATE_LIMIT_EXCEEDED,
    SECURITY_POLICY_VIOLATION,
    
    // Eventos de Sistema
    SYSTEM_CONFIGURATION_CHANGED,
    BACKUP_CREATED,
    BACKUP_RESTORED,
    MAINTENANCE_MODE_ENABLED,
    MAINTENANCE_MODE_DISABLED,
    
    // Eventos de Datos
    DATA_EXPORTED,
    DATA_IMPORTED,
    SENSITIVE_DATA_ACCESSED,
    DATA_DELETION_REQUESTED,
    
    // Eventos de API
    API_KEY_CREATED,
    API_KEY_REVOKED,
    API_RATE_LIMIT_EXCEEDED,
    API_UNAUTHORIZED_ACCESS,

    //Eventos de negocio
    BUSINESS_PROCESS,
    BUSINESS_EVENT,
    BUSINESS_ERROR;
}