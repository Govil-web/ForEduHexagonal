package com.academia.infrastructure.web.controllers;

import com.academia.domain.ports.in.commands.RegisterNewStudentCommand;
import com.academia.domain.ports.in.dtos.StudentDetailsDTO;
import com.academia.domain.ports.in.student.RegisterNewStudentUseCase;
import com.academia.infrastructure.web.mappers.RegisterStudentRequestMapper;
import com.academia.infrastructure.web.requests.RegisterStudentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final RegisterNewStudentUseCase registerNewStudentUseCase;
    private final RegisterStudentRequestMapper requestMapper; // Spring ahora puede inyectar esto

    @PostMapping("/register")
    public ResponseEntity<StudentDetailsDTO> registerStudent(@Valid @RequestBody RegisterStudentRequest request) {
        // 1. Mapear el Request HTTP a un Comando del Dominio (Ahora funciona)
        RegisterNewStudentCommand command = requestMapper.toCommand(request);

        // 2. Invocar el Caso de Uso (el puerto de entrada)
        StudentDetailsDTO studentDetails = registerNewStudentUseCase.registerStudent(command);

        // 3. Devolver la respuesta
        return ResponseEntity.status(HttpStatus.CREATED).body(studentDetails);
    }
}