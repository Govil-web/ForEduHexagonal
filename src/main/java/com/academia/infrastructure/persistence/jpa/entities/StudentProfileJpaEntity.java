package com.academia.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private Long userId; // Clave primaria y foránea a la vez
    private Long organizationId;
    private String studentIdNumber;
    private LocalDate enrollmentDate;
    private String currentGradeLevel;
}