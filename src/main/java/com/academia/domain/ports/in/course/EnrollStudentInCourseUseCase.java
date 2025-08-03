package com.academia.domain.ports.in.course;

import com.academia.domain.ports.in.commands.EnrollStudentInCourseCommand;
import com.academia.domain.ports.in.dtos.EnrollmentResultDTO;

public interface EnrollStudentInCourseUseCase {
    /**
     * Inscribe a un estudiante en un curso.
     * @param command El comando con los IDs del estudiante y del curso.
     * @return Un DTO con el resultado de la inscripción.
     * @throws com.academia.domain.exceptions.BusinessException si la inscripción no es posible.
     */
    EnrollmentResultDTO enrollStudent(EnrollStudentInCourseCommand command);
}