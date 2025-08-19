package com.academia.domain.ports.in.commands;

import java.util.UUID;

/**
 * Comando que representa la intención de inscribir un estudiante en un curso.
 */
public record EnrollStudentInCourseCommand(
        UUID studentAccountId,
        UUID courseId
) {}