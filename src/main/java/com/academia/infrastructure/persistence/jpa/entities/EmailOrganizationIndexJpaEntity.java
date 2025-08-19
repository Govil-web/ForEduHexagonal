package com.academia.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad JPA que mapea la tabla email_organization_index.
 * Proporciona un índice global de emails para todas las organizaciones.
 */
@Entity
@Table(name = "email_organization_index")
@Getter
@Setter
public class EmailOrganizationIndexJpaEntity {

    @Id
    @Column(name = "id", length = 36, columnDefinition = "CHAR(36)")
    private java.util.UUID id;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "organization_id", nullable = false, length = 36, columnDefinition = "CHAR(36)")
    private java.util.UUID organizationId;

    @Column(name = "user_id", nullable = false, length = 36, columnDefinition = "CHAR(36)")
    private java.util.UUID userId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Constructor por defecto requerido por JPA.
     */
    public EmailOrganizationIndexJpaEntity() {
    }

    /**
     * Constructor para crear una nueva entrada en el índice.
     *
     * @param email El email del usuario (único globalmente)
     * @param organizationId El ID de la organización a la que pertenece el usuario
     * @param userId El ID del usuario
     */
    public EmailOrganizationIndexJpaEntity(String email, java.util.UUID organizationId, java.util.UUID userId) {
        this.email = email;
        this.organizationId = organizationId;
        this.userId = userId;
    }

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = java.util.UUID.randomUUID();
        }
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}