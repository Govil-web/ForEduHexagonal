-- =================================================================
-- MIGRACIÓN V4: DATOS DE PRUEBA PARA MÓDULOS AVANZADOS
-- Puebla las tablas de tareas, finanzas y comunicación creadas en V3.
-- =================================================================

-- -----------------------------------------------------
-- OBTENER IDs DEL CONTEXTO DE V2
-- -----------------------------------------------------
SET @course_primaria_id = (SELECT id FROM courses WHERE course_code = 'MAT3-2025');
-- CORRECCIÓN: Se especifica e.id para evitar ambigüedad.
SET @enrollment_carlos_id = (SELECT e.id FROM enrollments e JOIN users u ON e.student_user_id = u.id WHERE u.email = 'carlos.ruiz@pequenomundo.edu' AND e.course_id = @course_primaria_id);
SET @teacher_primaria_id = (SELECT id FROM users WHERE email = 'laura.gomez@pequenomundo.edu');

SET @course_uni_id = (SELECT id FROM courses WHERE course_code = 'CS101-2025-1');
-- CORRECCIÓN: Se especifica e.id para evitar ambigüedad.
SET @enrollment_luis_id = (SELECT e.id FROM enrollments e JOIN users u ON e.student_user_id = u.id WHERE u.email = 'luis.vega@unifuturo.edu' AND e.course_id = @course_uni_id);
SET @teacher_uni_id = (SELECT id FROM users WHERE email = 'ricardo.turing@unifuturo.edu');

-- -----------------------------------------------------
-- Módulo Pedagógico: Tareas, Entregas y Calificaciones
-- -----------------------------------------------------

-- Tarea para el curso de Algoritmos de la Universidad
INSERT INTO assignments (course_id, title, description, due_date, max_points) VALUES
    (@course_uni_id, 'Taller 1: Listas Enlazadas', 'Implementar una lista doblemente enlazada en Java.', '2025-03-15 23:59:59', 100.00);
SET @assignment_uni_id = LAST_INSERT_ID();

-- Entrega del estudiante Luis Vega para esa tarea
INSERT INTO submissions (assignment_id, enrollment_id, content, notes_from_student) VALUES
    (@assignment_uni_id, @enrollment_luis_id, 'https://github.com/luisvega/listas-enlazadas', 'Adjunto el enlace al repositorio.');
SET @submission_luis_id = LAST_INSERT_ID();

-- Calificación del profesor para la entrega de Luis Vega
INSERT INTO grades (submission_id, enrollment_id, grade, feedback_from_teacher, teacher_user_id) VALUES
    (@submission_luis_id, @enrollment_luis_id, 95.50, 'Excelente trabajo, Luis. El código es limpio y eficiente.', @teacher_uni_id);

-- -----------------------------------------------------
-- Módulo Financiero: Cargos y Pagos
-- -----------------------------------------------------

-- Plantilla de cargo para la Primaria
INSERT INTO fee_templates (organization_id, name, description, default_amount) VALUES
    (1, 'Matrícula Anual 2025', 'Costo de la matrícula para el año lectivo 2025.', 500.00);
SET @fee_template_primaria_id = LAST_INSERT_ID();

-- Cargo generado para Carlos Ruiz (obteniendo su ID por email)
SET @carlos_id = (SELECT id FROM users WHERE email = 'carlos.ruiz@pequenomundo.edu');
INSERT INTO fees (student_user_id, template_id, description, amount, due_date, status) VALUES
    (@carlos_id, @fee_template_primaria_id, 'Matrícula Anual 2025 - Carlos Ruiz', 500.00, '2025-02-28', 'PAID');
SET @fee_carlos_id = LAST_INSERT_ID();

-- Pago realizado por su tutora (obteniendo su ID por email)
SET @mariana_id = (SELECT id FROM users WHERE email = 'mariana.ruiz@email.com');
INSERT INTO payments (fee_id, paid_by_user_id, amount, method, transaction_id) VALUES
    (@fee_carlos_id, @mariana_id, 500.00, 'CREDIT_CARD', 'ch_123456789ABCDEF');


-- -----------------------------------------------------
-- Módulo de Comunicación: Anuncios y Foros
-- -----------------------------------------------------

-- Anuncio global para la Universidad del Rector (obteniendo su ID por email)
SET @rector_id = (SELECT id FROM users WHERE email = 'rector.morales@unifuturo.edu');
INSERT INTO announcements (organization_id, author_user_id, title, content, is_pinned) VALUES
    (2, @rector_id, '¡Bienvenidos al Semestre 2025-1!', 'La Universidad del Futuro les da la bienvenida. Consulten el calendario académico.', TRUE);

-- Hilo en el foro del curso de Algoritmos
INSERT INTO forum_threads (course_id, created_by_user_id, title) VALUES
    (@course_uni_id, @teacher_uni_id, 'Dudas sobre la complejidad de algoritmos (Big O)');
SET @thread_id = LAST_INSERT_ID();

-- Publicación del estudiante Luis Vega (obteniendo su ID por email)
SET @luis_id = (SELECT id FROM users WHERE email = 'luis.vega@unifuturo.edu');
INSERT INTO forum_posts (thread_id, author_user_id, content) VALUES
    (@thread_id, @luis_id, 'Profesor, tengo una duda sobre la diferencia entre O(n log n) y O(n^2).');
SET @parent_post_id = LAST_INSERT_ID();

-- Respuesta del profesor al estudiante
INSERT INTO forum_posts (thread_id, author_user_id, content, parent_post_id) VALUES
    (@thread_id, @teacher_uni_id, 'Claro, Luis. Lo veremos en la próxima clase.', @parent_post_id);