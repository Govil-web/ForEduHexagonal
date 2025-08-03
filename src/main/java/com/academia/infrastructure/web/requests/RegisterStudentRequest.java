package com.academia.infrastructure.web.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * DTO (Data Transfer Object) que representa el cuerpo de la petición HTTP
 * para registrar un nuevo estudiante. Incluye validaciones a nivel de API.
 */
public record RegisterStudentRequest(
        @NotNull
        Long organizationId,

        @NotBlank @Size(min = 2, max = 50)
        String firstName,

        @NotBlank @Size(min = 2, max = 50)
        String lastName,

        @NotBlank @Email
        String email,

        @NotNull @Past
        LocalDate birthDate,

        @NotBlank @Size(min = 1, max = 20)
        String studentIdNumber, // Legajo

        @NotNull
        LocalDate enrollmentDate,

        @NotBlank
        String initialGradeLevel
) {}