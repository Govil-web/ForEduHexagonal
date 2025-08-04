-- =================================================================
-- MIGRACIÓN V2: DATOS DE PRUEBA COMPLETOS Y FUNCIONALES
-- Crea un ecosistema básico y coherente para cada organización.
-- =================================================================

-- -----------------------------------------------------
-- Roles del Sistema y de Tenant (Sin cambios)
-- -----------------------------------------------------
INSERT INTO roles (id, name, scope) VALUES
                                        (1, 'SYSTEM_ADMIN', 'SYSTEM'),
                                        (2, 'ORGANIZATION_ADMIN', 'TENANT'),
                                        (3, 'ACADEMIC_DIRECTOR', 'TENANT'),
                                        (4, 'TEACHER', 'TENANT'),
                                        (5, 'STUDENT', 'TENANT'),
                                        (6, 'GUARDIAN', 'TENANT');

-- -----------------------------------------------------
-- Super User (Administrador de la Plataforma) (Sin cambios)
-- Pass: superadmin123
-- -----------------------------------------------------
INSERT INTO users (id, organization_id, first_name, last_name, email, password_hash, birth_date, account_status) VALUES
    (1, NULL, 'Plataforma', 'Admin', 'superadmin@plataforma.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '1990-01-01', 'ACTIVE');
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);


-- =====================================================
-- TENANT 1: Primaria "Mi Pequeño Mundo"
-- =====================================================
INSERT INTO organizations (id, uuid, name, subdomain, digital_consent_age) VALUES (1, UUID(), 'Primaria Mi Pequeño Mundo', 'pequenomundo', 18);

-- Admin de la Organización
-- Pass: adminmundo123
INSERT INTO users (organization_id, first_name, last_name, email, password_hash, birth_date, account_status) VALUES
    (1, 'Ana', 'Directora', 'directora.ana@pequenomundo.edu', '$2a$10$Y.aV.qCgfj4j.mCj/wJGm.U/4J4.gY5.e/3.4h5gQh/e6gQ2iV1Z.', '1980-05-10', 'ACTIVE');
SET @admin_mundo_id = LAST_INSERT_ID();
INSERT INTO user_roles (user_id, role_id) VALUES (@admin_mundo_id, 2);
INSERT INTO staff_profiles (user_id, title) VALUES (@admin_mundo_id, 'Directora General');

-- **NUEVO**: Profesor para la Primaria
-- Pass: profeprimaria123
INSERT INTO users (organization_id, first_name, last_name, email, password_hash, birth_date, account_status) VALUES
    (1, 'Laura', 'Gomez', 'laura.gomez@pequenomundo.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '1992-03-15', 'ACTIVE');
SET @teacher_primaria_id = LAST_INSERT_ID();
INSERT INTO user_roles (user_id, role_id) VALUES (@teacher_primaria_id, 4);
INSERT INTO staff_profiles (user_id, title) VALUES (@teacher_primaria_id, 'Profesora de 3er Grado');

-- Student MENOR de edad y su Tutor (Sin cambios)
INSERT INTO users (organization_id, first_name, last_name, email, birth_date, account_status) VALUES (1, 'Carlos', 'Ruiz', 'carlos.ruiz@pequenomundo.edu', '2016-08-20', 'TUTOR_MANAGED');
SET @carlos_id = LAST_INSERT_ID();
INSERT INTO user_roles (user_id, role_id) VALUES (@carlos_id, 5);
INSERT INTO student_profiles (user_id, organization_id, student_id_number, enrollment_date, current_grade_level) VALUES (@carlos_id, 1, 'PM-2024-001', '2024-02-01', '3er Grado');

INSERT INTO users (organization_id, first_name, last_name, email, password_hash, birth_date, account_status) VALUES (1, 'Mariana', 'Ruiz', 'mariana.ruiz@email.com', '$2a$10$Y.aV.qCgfj4j.mCj/wJGm.U/4J4.gY5.e/3.4h5gQh/e6gQ2iV1Z.', '1988-11-05', 'ACTIVE');
SET @mariana_id = LAST_INSERT_ID();
INSERT INTO user_roles (user_id, role_id) VALUES (@mariana_id, 6);
INSERT INTO guardian_profiles (user_id, occupation, is_financial_responsible) VALUES (@mariana_id, 'Abogada', TRUE);
INSERT INTO student_guardian_relationships (student_user_id, guardian_user_id, relationship_type, is_primary_contact) VALUES (@carlos_id, @mariana_id, 'Madre', TRUE);

-- **NUEVO**: Estructura académica para la Primaria
INSERT INTO subjects (organization_id, name, subject_code, grade_level) VALUES (1, 'Matemáticas 3', 'MAT-3', '3er Grado');
SET @subject_primaria_id = LAST_INSERT_ID();
INSERT INTO academic_terms (organization_id, name, start_date, end_date) VALUES (1, 'Año Lectivo 2025', '2025-02-01', '2025-11-30');
SET @term_primaria_id = LAST_INSERT_ID();
INSERT INTO courses (subject_id, academic_term_id, teacher_user_id, course_code) VALUES (@subject_primaria_id, @term_primaria_id, @teacher_primaria_id, 'MAT3-2025');
SET @course_primaria_id = LAST_INSERT_ID();
INSERT INTO enrollments (student_user_id, course_id, status) VALUES (@carlos_id, @course_primaria_id, 'ACTIVE');


-- =====================================================
-- TENANT 2: "Universidad del Futuro"
-- =====================================================
INSERT INTO organizations (id, uuid, name, subdomain, digital_consent_age) VALUES (2, UUID(), 'Universidad del Futuro', 'unifuturo', 16);

-- Admin de la Organización (Sin cambios)
INSERT INTO users (organization_id, first_name, last_name, email, password_hash, birth_date, account_status) VALUES
    (2, 'Rector', 'Morales', 'rector.morales@unifuturo.edu', '$2a$10$Y.aV.qCgfj4j.mCj/wJGm.U/4J4.gY5.e/3.4h5gQh/e6gQ2iV1Z.', '1970-01-25', 'ACTIVE');
SET @admin_futuro_id = LAST_INSERT_ID();
INSERT INTO user_roles (user_id, role_id) VALUES (@admin_futuro_id, 2);
INSERT INTO staff_profiles (user_id, title) VALUES (@admin_futuro_id, 'Rector');

-- **NUEVO**: Profesor para la Universidad
-- Pass: profeunifuturo123
INSERT INTO users (organization_id, first_name, last_name, email, password_hash, birth_date, account_status) VALUES
    (2, 'Ricardo', 'Turing', 'ricardo.turing@unifuturo.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '1985-06-12', 'ACTIVE');
SET @teacher_uni_id = LAST_INSERT_ID();
INSERT INTO user_roles (user_id, role_id) VALUES (@teacher_uni_id, 4);
INSERT INTO staff_profiles (user_id, employee_id_number, hire_date, title) VALUES (@teacher_uni_id, 'EMP-UF-001', '2022-08-01', 'Profesor de Ingeniería de Software');

-- Student MAYOR de edad relativa (Sin cambios)
-- Pass: luisvega123
INSERT INTO users (organization_id, first_name, last_name, email, password_hash, birth_date, account_status) VALUES
    (2, 'Luis', 'Vega', 'luis.vega@unifuturo.edu', '$2a$10$Y.aV.qCgfj4j.mCj/wJGm.U/4J4.gY5.e/3.4h5gQh/e6gQ2iV1Z.', '2008-05-10', 'ACTIVE');
SET @luis_id = LAST_INSERT_ID();
INSERT INTO user_roles (user_id, role_id) VALUES (@luis_id, 5);
INSERT INTO student_profiles (user_id, organization_id, student_id_number, enrollment_date, current_grade_level) VALUES (@luis_id, 2, 'UF-2025-001', '2025-02-01', 'Arquitectura - Semestre 1');

-- **NUEVO**: Estructura académica para la Universidad
INSERT INTO subjects (organization_id, name, subject_code, grade_level) VALUES (2, 'Algoritmos y Estructuras de Datos', 'CS-101', 'Ingeniería de Software - Semestre 1');
SET @subject_uni_id = LAST_INSERT_ID();
INSERT INTO academic_terms (organization_id, name, start_date, end_date) VALUES (2, 'Semestre 2025-1', '2025-02-01', '2025-06-15');
SET @term_uni_id = LAST_INSERT_ID();
INSERT INTO courses (subject_id, academic_term_id, teacher_user_id, course_code) VALUES (@subject_uni_id, @term_uni_id, @teacher_uni_id, 'CS101-2025-1');
SET @course_uni_id = LAST_INSERT_ID();
INSERT INTO enrollments (student_user_id, course_id, status) VALUES (@luis_id, @course_uni_id, 'ACTIVE');