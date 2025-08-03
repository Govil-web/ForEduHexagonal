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
        // Lógica de ejemplo:
        // 1. Verificar si el grado del estudiante es compatible con el del curso.
        // 2. Consultar el historial de inscripciones del estudiante (a través de un repositorio)
        //    para ver si ha aprobado las materias pre-requisito.
        // 3. Verificar si hay cupos disponibles en el curso (si esa lógica estuviera aquí).

        return true; // Simplificado por ahora
    }
}
