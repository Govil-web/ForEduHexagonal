package com.academia.domain.ports.in.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO que representa el resultado de una inscripción exitosa.
 */
public record EnrollmentResultDTO(
        Long enrollmentId,
        UUID studentAccountId,
        UUID courseId,
        String status,
        LocalDateTime enrollmentDate
) {}