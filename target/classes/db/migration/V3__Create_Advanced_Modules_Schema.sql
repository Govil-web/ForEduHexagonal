-- =================================================================
-- MIGRACIÓN V3: MÓDULOS AVANZADOS PARA FINANZAS, PEDAGOGÍA Y COMUNICACIÓN
-- Diseñado para extender la funcionalidad del sistema académico.
-- =================================================================

-- -----------------------------------------------------
-- Módulo: Herramientas Pedagógicas
-- Se enfoca en la gestión de tareas, entregas y calificaciones detalladas.
-- -----------------------------------------------------

-- Tabla: assignments (Tareas y Actividades)
-- Define una tarea o actividad asignada dentro de un curso específico.
CREATE TABLE assignments (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             course_id BIGINT NOT NULL,
                             title VARCHAR(255) NOT NULL,
                             description TEXT,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                             due_date TIMESTAMP,
                             max_points DECIMAL(5,2),
                             FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
                             INDEX idx_assignments_course_id (course_id)
);

-- Tabla: submissions (Entregas de Estudiantes)
-- Almacena la entrega de un estudiante para una tarea específica.
CREATE TABLE submissions (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             assignment_id BIGINT NOT NULL,
                             enrollment_id BIGINT NOT NULL, -- Vincula la entrega a la inscripción del estudiante
                             submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                             content TEXT, -- Puede ser un texto o un enlace a un archivo
                             notes_from_student TEXT,
                             FOREIGN KEY (assignment_id) REFERENCES assignments(id) ON DELETE CASCADE,
                             FOREIGN KEY (enrollment_id) REFERENCES enrollments(id) ON DELETE CASCADE,
                             UNIQUE KEY uk_submission_assignment_enrollment (assignment_id, enrollment_id)
);

-- Tabla: grades (Calificaciones Detalladas)
-- Guarda la calificación específica de una entrega (o cualquier item calificable).
-- Esto permite tener múltiples notas que componen la 'final_grade' de la inscripción.
CREATE TABLE grades (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        submission_id BIGINT UNIQUE, -- Una entrega puede tener una sola calificación
                        enrollment_id BIGINT NOT NULL,
                        grade DECIMAL(5,2) NOT NULL,
                        graded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                        feedback_from_teacher TEXT,
                        teacher_user_id BIGINT,
                        FOREIGN KEY (submission_id) REFERENCES submissions(id) ON DELETE CASCADE,
                        FOREIGN KEY (enrollment_id) REFERENCES enrollments(id) ON DELETE CASCADE,
                        FOREIGN KEY (teacher_user_id) REFERENCES staff_profiles(user_id) ON DELETE SET NULL
);


-- -----------------------------------------------------
-- Módulo: Finanzas y Pagos
-- Gestiona las obligaciones financieras de los estudiantes y tutores.
-- -----------------------------------------------------

-- Tabla: fee_templates (Plantillas de Cargos)
-- Permite a los administradores crear tipos de cargos recurrentes (ej. "Matrícula Anual", "Cuota Mensual").
CREATE TABLE fee_templates (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               organization_id BIGINT NOT NULL,
                               name VARCHAR(100) NOT NULL,
                               description TEXT,
                               default_amount DECIMAL(10, 2) NOT NULL,
                               is_active BOOLEAN DEFAULT TRUE,
                               FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE
);

-- Tabla: fees (Cargos Financieros)
-- Representa una obligación de pago específica para un estudiante.
-- Se puede generar a partir de una plantilla o ser un cargo único.
CREATE TABLE fees (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      student_user_id BIGINT NOT NULL,
                      template_id BIGINT, -- Opcional, si se basa en una plantilla
                      description VARCHAR(255) NOT NULL,
                      amount DECIMAL(10, 2) NOT NULL,
                      due_date DATE NOT NULL,
                      status ENUM('PENDING', 'PAID', 'PARTIALLY_PAID', 'OVERDUE', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                      FOREIGN KEY (student_user_id) REFERENCES student_profiles(user_id) ON DELETE CASCADE,
                      FOREIGN KEY (template_id) REFERENCES fee_templates(id) ON DELETE SET NULL,
                      INDEX idx_fees_student_status (student_user_id, status)
);

-- Tabla: payments (Pagos)
-- Registra un pago realizado por un estudiante/tutor.
CREATE TABLE payments (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          fee_id BIGINT NOT NULL,
                          paid_by_user_id BIGINT, -- Quién realizó el pago (puede ser un tutor)
                          amount DECIMAL(10, 2) NOT NULL,
                          payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                          method ENUM('CREDIT_CARD', 'BANK_TRANSFER', 'CASH', 'OTHER') NOT NULL,
                          transaction_id VARCHAR(255), -- Para referencias externas (ej. Stripe ID)
                          notes TEXT,
                          FOREIGN KEY (fee_id) REFERENCES fees(id) ON DELETE CASCADE,
                          FOREIGN KEY (paid_by_user_id) REFERENCES users(id) ON DELETE SET NULL
);


-- -----------------------------------------------------
-- Módulo: Comunicación y Comunidad
-- Facilita la interacción dentro de la plataforma.
-- -----------------------------------------------------

-- Tabla: announcements (Anuncios)
-- Para noticias y comunicados a nivel de organización o de curso.
CREATE TABLE announcements (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               organization_id BIGINT NOT NULL,
                               author_user_id BIGINT,
                               course_id BIGINT, -- NULO si es un anuncio para toda la organización
                               title VARCHAR(255) NOT NULL,
                               content TEXT NOT NULL,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                               is_pinned BOOLEAN DEFAULT FALSE,
                               FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
                               FOREIGN KEY (author_user_id) REFERENCES users(id) ON DELETE SET NULL,
                               FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- Tabla: forum_threads (Hilos del Foro)
-- Un hilo de discusión dentro del foro de un curso.
CREATE TABLE forum_threads (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               course_id BIGINT NOT NULL,
                               created_by_user_id BIGINT,
                               title VARCHAR(255) NOT NULL,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                               is_locked BOOLEAN DEFAULT FALSE,
                               FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
                               FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Tabla: forum_posts (Publicaciones en el Foro)
-- Una respuesta o publicación dentro de un hilo de discusión.
CREATE TABLE forum_posts (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             thread_id BIGINT NOT NULL,
                             author_user_id BIGINT,
                             content TEXT NOT NULL,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                             parent_post_id BIGINT, -- Para respuestas anidadas
                             FOREIGN KEY (thread_id) REFERENCES forum_threads(id) ON DELETE CASCADE,
                             FOREIGN KEY (author_user_id) REFERENCES users(id) ON DELETE SET NULL,
                             FOREIGN KEY (parent_post_id) REFERENCES forum_posts(id) ON DELETE CASCADE
);