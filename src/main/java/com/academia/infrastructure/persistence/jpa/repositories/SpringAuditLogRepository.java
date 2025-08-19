package com.academia.infrastructure.persistence.jpa.repositories;

import com.academia.domain.model.enums.AuditEventType;
import com.academia.infrastructure.persistence.jpa.entities.AuditLogJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpringAuditLogRepository extends JpaRepository<AuditLogJpaEntity, java.util.UUID> {

    List<AuditLogJpaEntity> findByUserIdAndTimestampBetween(java.util.UUID userId, LocalDateTime startDate, LocalDateTime endDate);

    List<AuditLogJpaEntity> findByOrganizationIdAndTimestampBetween(java.util.UUID organizationId, LocalDateTime startDate, LocalDateTime endDate);

    List<AuditLogJpaEntity> findByEventTypeAndTimestampAfter(AuditEventType eventType, LocalDateTime since);

    List<AuditLogJpaEntity> findByIpAddressAndTimestampAfter(String ipAddress, LocalDateTime since);

    @Query("SELECT a FROM AuditLogJpaEntity a WHERE a.userEmail = :email AND a.success = false AND a.timestamp > :since")
    List<AuditLogJpaEntity> findFailedLoginsByEmail(@Param("email") String email, @Param("since") LocalDateTime since);

    @Query("SELECT a FROM AuditLogJpaEntity a WHERE a.eventType IN :criticalTypes AND a.timestamp > :since")
    List<AuditLogJpaEntity> findHighCriticalityEvents(@Param("criticalTypes") List<AuditEventType> criticalTypes, @Param("since") LocalDateTime since);

    @Query("SELECT a FROM AuditLogJpaEntity a WHERE a.ipAddress = :ipAddress AND a.timestamp > :since GROUP BY a.eventType HAVING COUNT(*) >= :minEventCount")
    List<AuditLogJpaEntity> findSuspiciousActivityByIp(@Param("ipAddress") String ipAddress, @Param("since") LocalDateTime since, @Param("minEventCount") int minEventCount);

    long countByEventTypeAndTimestampBetween(AuditEventType eventType, LocalDateTime startDate, LocalDateTime endDate);

    void deleteByTimestampBefore(LocalDateTime cutoffDate);

    @Query("SELECT a FROM AuditLogJpaEntity a WHERE a.userId = :userId AND a.success = true AND a.eventType = 'LOGIN_SUCCESS' ORDER BY a.timestamp DESC LIMIT 1")
    Optional<AuditLogJpaEntity> findLastSuccessfulLoginByUserId(@Param("userId") java.util.UUID userId);
}