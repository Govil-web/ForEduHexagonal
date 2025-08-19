package com.academia.domain.ports.in.commands;

import java.time.LocalDate;

/**
 * Comando inmutable que representa la intención de registrar un nuevo estudiante.
 * Contiene todos los datos necesarios para completar la operación.
 */
public record RegisterNewStudentCommand(
        Long organizationId,
        String firstName,
        String lastName,
        String email,
        LocalDate birthDate,
        String studentIdNumber, // Legajo
        LocalDate enrollmentDate,
        String initialGradeLevel
) {}
