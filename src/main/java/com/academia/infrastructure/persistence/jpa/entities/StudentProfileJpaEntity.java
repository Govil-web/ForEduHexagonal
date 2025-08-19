package com.academia.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

// ... imports
@Entity
@Table(name = "student_profiles")
@Getter
@Setter
public class StudentProfileJpaEntity {
    @Id
    @Column(name = "user_id", length = 36, columnDefinition = "CHAR(36)")
    private java.util.UUID userId; // Clave primaria y foránea a la vez
    
    @Column(name = "organization_id", length = 36, columnDefinition = "CHAR(36)")
    private java.util.UUID organizationId;
    @Column(name = "student_id_number")
    private String studentIdNumber;
    
    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;
    
    @Column(name = "current_grade_level")
    private String currentGradeLevel;
}