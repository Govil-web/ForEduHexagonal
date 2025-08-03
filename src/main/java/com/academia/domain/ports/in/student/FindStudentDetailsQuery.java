package com.academia.domain.ports.in.student;

import com.academia.domain.ports.in.queries.GetStudentDetailsQuery;
import com.academia.domain.ports.in.dtos.StudentDetailsDTO;
import java.util.Optional;

public interface FindStudentDetailsQuery {
    /**
     * Busca y devuelve los detalles de un estudiante específico.
     * @param query La consulta con el ID del estudiante.
     * @return Un Optional que contiene el DTO del estudiante si se encuentra.
     */
    Optional<StudentDetailsDTO> findStudentDetails(GetStudentDetailsQuery query);
}