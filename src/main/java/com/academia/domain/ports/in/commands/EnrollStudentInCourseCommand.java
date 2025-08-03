package com.academia.domain.ports.in.commands;

/**
 * Comando que representa la intención de inscribir un estudiante en un curso.
 */
public record EnrollStudentInCourseCommand(
        Long studentAccountId,
        Long courseId
) {}