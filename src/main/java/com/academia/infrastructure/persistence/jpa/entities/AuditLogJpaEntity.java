package com.academia.infrastructure.persistence.jpa.entities;

import com.academia.domain.model.enums.AuditEventType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
public class AuditLogJpaEntity {

    @Id
    @Column(name = "id", length = 36, columnDefinition = "CHAR(36)")
    private java.util.UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private AuditEventType eventType;

    @Column(name = "user_id", length = 36, columnDefinition = "CHAR(36)")
    private java.util.UUID userId;

    @Column(name = "organization_id", length = 36, columnDefinition = "CHAR(36)")
    private java.util.UUID organizationId;

    @Column(name = "user_email", length = 255)
    private String userEmail;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "resource", length = 255)
    private String resource;

    @Column(name = "action", length = 100)
    private String action;

    @Column(name = "success", nullable = false)
    private boolean success;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Column(name = "request_id", length = 100)
    private String requestId;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = java.util.UUID.randomUUID();
        }
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}