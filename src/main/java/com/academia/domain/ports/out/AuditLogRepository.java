package com.academia.domain.ports.out;

import com.academia.domain.model.entities.AuditLog;
import com.academia.domain.model.enums.AuditEventType;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para el repositorio de logs de auditoría.
 */
public interface AuditLogRepository {
    
    /**
     * Guarda un evento de auditoría.
     */
    AuditLog save(AuditLog auditLog);
    
    /**
     * Encuentra logs por ID de usuario en un rango de fechas.
     */
    List<AuditLog> findByUserIdAndDateRange(
        AccountId userId, 
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
    
    /**
     * Encuentra logs por organización en un rango de fechas.
     */
    List<AuditLog> findByOrganizationIdAndDateRange(
        OrganizationId organizationId,
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
    
    /**
     * Encuentra logs por tipo de evento.
     */
    List<AuditLog> findByEventType(AuditEventType eventType, LocalDateTime since);
    
    /**
     * Encuentra logs por dirección IP.
     */
    List<AuditLog> findByIpAddress(String ipAddress, LocalDateTime since);
    
    /**
     * Encuentra intentos de login fallidos por email.
     */
    List<AuditLog> findFailedLoginsByEmail(String email, LocalDateTime since);
    
    /**
     * Encuentra eventos de alta criticidad.
     */
    List<AuditLog> findHighCriticalityEvents(LocalDateTime since);
    
    /**
     * Encuentra eventos sospechosos basados en patrones.
     */
    List<AuditLog> findSuspiciousActivity(
        String ipAddress, 
        LocalDateTime since,
        int minEventCount
    );
    
    /**
     * Cuenta eventos por tipo en un período.
     */
    long countByEventTypeAndDateRange(
        AuditEventType eventType,
        LocalDateTime startDate,
        LocalDateTime endDate
    );
    
    /**
     * Elimina logs antiguos (para cumplir políticas de retención).
     */
    void deleteOlderThan(LocalDateTime cutoffDate);
    
    /**
     * Encuentra el último evento exitoso de un usuario.
     */
    Optional<AuditLog> findLastSuccessfulLoginByUserId(AccountId userId);
}