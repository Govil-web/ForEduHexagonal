package com.academia.domain.ports.in.queries;

import java.util.UUID;

/**
 * Consulta que representa la solicitud de los detalles de un estudiante.
 */
public record GetStudentDetailsQuery(
        UUID studentAccountId
) {}