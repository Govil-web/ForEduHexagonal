package com.academia.application.services;

import com.academia.application.exceptions.ResourceNotFoundException;
import com.academia.domain.model.aggregates.Course;
import com.academia.domain.model.entities.Enrollment;
import com.academia.domain.model.entities.Student;
import com.academia.domain.model.services.EnrollmentEligibilityChecker;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.CourseId;
import com.academia.domain.ports.in.commands.EnrollStudentInCourseCommand;
import com.academia.domain.ports.in.dtos.EnrollmentResultDTO;
import com.academia.domain.ports.in.course.EnrollStudentInCourseUseCase;
import com.academia.domain.ports.out.CourseRepository;
import com.academia.domain.ports.out.DomainEventPublisher;
import com.academia.domain.ports.out.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EnrollStudentInCourseServiceImpl implements EnrollStudentInCourseUseCase {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final DomainEventPublisher domainEventPublisher;
    private final EnrollmentEligibilityChecker eligibilityChecker;

    @Override
    @Transactional
    public EnrollmentResultDTO enrollStudent(EnrollStudentInCourseCommand command) {
        AccountId studentId = new AccountId(command.studentAccountId());
        CourseId courseId = new CourseId(command.courseId());

        // Usamos las firmas corregidas
        Student student = studentRepository.findByAccountId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID de cuenta: " + studentId.getValue()));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + courseId.getValue()));

        // La llamada ahora es segura
        Enrollment newEnrollment = course.enrollStudent(student, eligibilityChecker);

        // Guardamos el agregado Course, que contiene la nueva inscripción
        courseRepository.save(course);

        // Publicamos los eventos con el tipo correcto
        domainEventPublisher.publish(course.getDomainEvents());
        course.clearDomainEvents();

        return new EnrollmentResultDTO(
                newEnrollment.getId(),
                newEnrollment.getStudentId().getValue(),
                newEnrollment.getCourseId().getValue(),
                newEnrollment.getStatus().name(),
                newEnrollment.getEnrollmentDate()
        );
    }
}