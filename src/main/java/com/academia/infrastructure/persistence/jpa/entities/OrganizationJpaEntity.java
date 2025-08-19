package com.academia.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "organizations")
@Getter
@Setter
public class OrganizationJpaEntity {

    @Id
    @Column(name = "id", length = 36, columnDefinition = "CHAR(36)")
    private java.util.UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String subdomain;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "digital_consent_age", nullable = false)
    private int digitalConsentAge = 18;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;

        // Generar ID UUID si no existe
        if (id == null) {
            id = java.util.UUID.randomUUID();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}