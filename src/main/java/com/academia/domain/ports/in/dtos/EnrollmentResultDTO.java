package com.academia.domain.ports.in.dtos;

import java.time.LocalDateTime;

/**
 * DTO que representa el resultado de una inscripción exitosa.
 */
public record EnrollmentResultDTO(
        Long enrollmentId,
        Long studentAccountId,
        Long courseId,
        String status,
        LocalDateTime enrollmentDate
) {}