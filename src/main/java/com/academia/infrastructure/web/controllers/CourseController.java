package com.academia.infrastructure.web.controllers;

import com.academia.domain.ports.in.commands.EnrollStudentInCourseCommand;
import com.academia.domain.ports.in.course.EnrollStudentInCourseUseCase;
import com.academia.domain.ports.in.dtos.EnrollmentResultDTO;
import com.academia.infrastructure.web.mappers.EnrollStudentRequestMapper;
import com.academia.infrastructure.web.requests.EnrollStudentInCourseRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final EnrollStudentInCourseUseCase enrollStudentUseCase;
    private final EnrollStudentRequestMapper requestMapper;

    @PostMapping("/enroll")
    public ResponseEntity<EnrollmentResultDTO> enrollStudent(@Valid @RequestBody EnrollStudentInCourseRequest request) {
        EnrollStudentInCourseCommand command = requestMapper.toCommand(request);
        EnrollmentResultDTO result = enrollStudentUseCase.enrollStudent(command);
        return ResponseEntity.ok(result);
    }
}
