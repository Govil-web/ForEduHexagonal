package com.academia.domain.ports.in.dtos;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * DTO que representa la vista de un estudiante para los clientes de la aplicación.
 * Es una estructura de datos estable, desacoplada del modelo de dominio interno.
 */
public record StudentDetailsDTO(
        UUID accountId,
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
            UUID guardianAccountId,
            String fullName,
            String relationshipType,
            boolean isPrimaryContact
    ) {}
}