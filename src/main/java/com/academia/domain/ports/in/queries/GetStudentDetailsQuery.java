package com.academia.domain.ports.in.queries;

/**
 * Consulta que representa la solicitud de los detalles de un estudiante.
 */
public record GetStudentDetailsQuery(
        Long studentAccountId
) {}