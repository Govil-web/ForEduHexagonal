package com.academia.domain.ports.in.dtos;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO que representa la vista de un estudiante para los clientes de la aplicación.
 * Es una estructura de datos estable, desacoplada del modelo de dominio interno.
 */
public record StudentDetailsDTO(
        Long accountId,
        String fullName,
        String email,
        int age,
        String studentIdNumber, // Legajo
        String currentGradeLevel,
        LocalDate enrollmentDate,
        String accountStatus,
        List<GuardianDTO> guardians
) {
    public record GuardianDTO(
            Long guardianAccountId,
            String fullName,
            String relationshipType,
            boolean isPrimaryContact
    ) {}
}