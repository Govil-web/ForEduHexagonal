# Módulo de Pedagogía

Este módulo contiene las herramientas para la gestión académica del día a día, como tareas, entregas y calificaciones.

## Modelo de Datos Clave
* **`assignments`**: Define una tarea o actividad dentro de un curso.
* **`submissions`**: Almacena la entrega de un estudiante para una tarea.
* **`grades`**: Guarda la calificación y el feedback para una entrega.

## Flujo de Trabajo Típico
1.  Un **Profesor** crea un `assignment` para su `course`.
2.  Los **Estudiantes** inscritos ven la tarea y realizan una `submission`.
3.  El **Profesor** revisa la `submission` y crea una `grade` con la nota y el feedback.
4.  La suma o promedio de las `grades` puede usarse para calcular la `final_grade` en la tabla `enrollments`.