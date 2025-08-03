package com.academia.domain.ports.out;

import com.academia.domain.model.aggregates.Course; // <-- TIPO CORREGIDO
import com.academia.domain.model.valueobjects.ids.CourseId;
import java.util.Optional;

public interface CourseRepository {
    /**
     * Guarda el estado completo del agregado del curso.
     * @param course El agregado del curso a persistir.
     * @return El agregado del curso persistido (puede incluir IDs actualizados).
     */
    Course save(Course course); // <-- TIPO CORREGIDO

    /**
     * Busca y reconstruye un agregado de curso por su ID.
     * @param courseId El ID del curso.
     * @return Un Optional que contiene el agregado del curso si se encuentra.
     */
    Optional<Course> findById(CourseId courseId);
}