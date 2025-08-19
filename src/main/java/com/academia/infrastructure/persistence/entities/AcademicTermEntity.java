package com.academia.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "academic_terms")
@Getter
@Setter
@NoArgsConstructor
public class AcademicTermEntity {
    
    @Id
    @Column(name = "id", length = 36, columnDefinition = "CHAR(36)")
    private UUID id;
    
    @Column(name = "organization_id", nullable = false, length = 36, columnDefinition = "CHAR(36)")
    private UUID organizationId;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "is_current_term", nullable = false)
    private Boolean isCurrentTerm = false;
    
    @Column(name = "created_at", nullable = false)
    private java.time.Instant createdAt;
    
    @Column(name = "updated_at")
    private java.time.Instant updatedAt;
    
    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        createdAt = java.time.Instant.now();
        updatedAt = java.time.Instant.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.Instant.now();
    }
}