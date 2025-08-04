package com.academia.domain.model.services;
import com.academia.domain.model.aggregates.Course;
import com.academia.domain.model.entities.Student;

/**
 * Servicio de Dominio para encapsular lógica que no pertenece a un único agregado.
 * En este caso, verificar la elegibilidad podría requerir datos del historial del estudiante
 * y datos del curso, que son dos agregados diferentes.
 */
public class EnrollmentEligibilityChecker {

    // En una implementación real, esto podría recibir repositorios en su constructor
    // para buscar el historial académico del estudiante.
    // private final StudentRepository studentRepository;
    // private final CourseRepository courseRepository;

    public boolean isStudentEligible(Student student, Course course) {
        // Regla 1: El estudiante y el curso deben pertenecer a la misma organización.
        if (!student.getOrganizationId().equals(course.getSubject().getOrganizationId())) {
            throw new IllegalStateException("El estudiante y el curso no pertenecen a la misma organización.");
        }

        // Regla 2: El estudiante no debe estar ya inscrito en el curso.
        boolean alreadyEnrolled = course.getEnrollments().stream()
                .anyMatch(enrollment -> enrollment.getStudentId().equals(student.getAccountId()));
        if (alreadyEnrolled) {
            throw new IllegalStateException("El estudiante ya está inscrito en este curso.");
        }

        // Lógica futura:
        // 1. Verificar si el grado del estudiante es compatible con el del curso.
        // 2. Consultar el historial de inscripciones para ver si ha aprobado pre-requisitos.
        // 3. Verificar si hay cupos disponibles.

        return true; // Simplificado por ahora
    }
}
