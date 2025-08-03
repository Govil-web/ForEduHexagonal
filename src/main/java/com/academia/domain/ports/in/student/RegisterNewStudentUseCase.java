package com.academia.domain.ports.in.student;

import com.academia.domain.ports.in.commands.RegisterNewStudentCommand;
import com.academia.domain.ports.in.dtos.StudentDetailsDTO;

public interface RegisterNewStudentUseCase {
    /**
     * Registra un nuevo estudiante en el sistema.
     * @param command El comando con los datos del estudiante.
     * @return Un DTO con los detalles del estudiante recién creado.
     * @throws com.academia.domain.exceptions.BusinessException si alguna regla de negocio no se cumple.
     */
    StudentDetailsDTO registerStudent(RegisterNewStudentCommand command);
}