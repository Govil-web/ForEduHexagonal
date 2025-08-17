package com.academia.infrastructure.persistence.adapters;

import com.academia.domain.model.entities.AuditLog;
import com.academia.domain.model.enums.AuditEventType;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.ports.out.AuditLogRepository;
import com.academia.infrastructure.persistence.jpa.entities.AuditLogJpaEntity;
import com.academia.infrastructure.persistence.jpa.mappers.AuditLogMapper;
import com.academia.infrastructure.persistence.jpa.repositories.SpringAuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JpaAuditLogRepositoryAdapter implements AuditLogRepository {

    private final SpringAuditLogRepository jpaRepository;
    private final AuditLogMapper mapper;

    @Override
    public AuditLog save(AuditLog auditLog) {
        try {
            AuditLogJpaEntity entity = mapper.toJpaEntity(auditLog);
            AuditLogJpaEntity savedEntity = jpaRepository.save(entity);
            return mapper.toDomain(savedEntity);
        } catch (Exception e) {
            log.error("Error guardando audit log: {}", e.getMessage(), e);
            // Retornar el audit log original si falla el guardado
            return auditLog;
        }
    }

    @Override
    public List<AuditLog> findByUserIdAndDateRange(AccountId userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<AuditLogJpaEntity> entities = jpaRepository.findByUserIdAndTimestampBetween(
            userId.getValue(), startDate, endDate);
        return entities.stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<AuditLog> findByOrganizationIdAndDateRange(OrganizationId organizationId, LocalDateTime startDate, LocalDateTime endDate) {
        List<AuditLogJpaEntity> entities = jpaRepository.findByOrganizationIdAndTimestampBetween(
            organizationId.getValue(), startDate, endDate);
        return entities.stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<AuditLog> findByEventType(AuditEventType eventType, LocalDateTime since) {
        List<AuditLogJpaEntity> entities = jpaRepository.findByEventTypeAndTimestampAfter(eventType, since);
        return entities.stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<AuditLog> findByIpAddress(String ipAddress, LocalDateTime since) {
        List<AuditLogJpaEntity> entities = jpaRepository.findByIpAddressAndTimestampAfter(ipAddress, since);
        return entities.stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<AuditLog> findFailedLoginsByEmail(String email, LocalDateTime since) {
        List<AuditLogJpaEntity> entities = jpaRepository.findFailedLoginsByEmail(email, since);
        return entities.stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<AuditLog> findHighCriticalityEvents(LocalDateTime since) {
        List<AuditEventType> criticalTypes = List.of(
            AuditEventType.LOGIN_BLOCKED,
            AuditEventType.PERMISSION_ESCALATION_ATTEMPT,
            AuditEventType.SUSPICIOUS_ACTIVITY_DETECTED,
            AuditEventType.BRUTE_FORCE_ATTEMPT,
            AuditEventType.SECURITY_POLICY_VIOLATION
        );
        List<AuditLogJpaEntity> entities = jpaRepository.findHighCriticalityEvents(criticalTypes, since);
        return entities.stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<AuditLog> findSuspiciousActivity(String ipAddress, LocalDateTime since, int minEventCount) {
        List<AuditLogJpaEntity> entities = jpaRepository.findSuspiciousActivityByIp(ipAddress, since, minEventCount);
        return entities.stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countByEventTypeAndDateRange(AuditEventType eventType, LocalDateTime startDate, LocalDateTime endDate) {
        return jpaRepository.countByEventTypeAndTimestampBetween(eventType, startDate, endDate);
    }

    @Override
    public void deleteOlderThan(LocalDateTime cutoffDate) {
        jpaRepository.deleteByTimestampBefore(cutoffDate);
    }

    @Override
    public Optional<AuditLog> findLastSuccessfulLoginByUserId(AccountId userId) {
        return jpaRepository.findLastSuccessfulLoginByUserId(userId.getValue())
            .map(mapper::toDomain);
    }
}