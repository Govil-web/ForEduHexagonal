-- =================================================================
-- MIGRACIÓN V2: DATOS DE PRUEBA COHERENTES Y FUNCIONALES
-- Contraseñas conocidas y documentadas para desarrollo
-- =================================================================

-- -----------------------------------------------------
-- Roles del sistema
-- -----------------------------------------------------
INSERT INTO roles (id, name, scope) VALUES
                                        (1, 'SYSTEM_ADMIN', 'SYSTEM'),
                                        (2, 'ORGANIZATION_ADMIN', 'TENANT'),
                                        (3, 'ACADEMIC_DIRECTOR', 'TENANT'),
                                        (4, 'TEACHER', 'TENANT'),
                                        (5, 'STUDENT', 'TENANT'),
                                        (6, 'GUARDIAN', 'TENANT');

-- -----------------------------------------------------
-- SUPER ADMIN (Sistema)
-- Email: superadmin@plataforma.com
-- Contraseña: 123456789
-- Hash: $2a$10$I0gnxgl3zHLeuDQWurpwce8/rvcwOAjCM0kOlVazBv9WVbrReovq6
-- -----------------------------------------------------
INSERT INTO users (id, organization_id, first_name, last_name, email, password_hash, birth_date, account_status)
VALUES (1, NULL, 'Super', 'Admin', 'superadmin@plataforma.com',
        '$2a$10$I0gnxgl3zHLeuDQWurpwce8/rvcwOAjCM0kOlVazBv9WVbrReovq6',
        '1985-01-01', 'ACTIVE');

INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);

-- =====================================================
-- ORGANIZACIÓN 1: "Universidad del Futuro"
-- =====================================================
INSERT INTO organizations (id, uuid, name, subdomain, digital_consent_age)
VALUES (1, 'org-unifuturo-001', 'Universidad del Futuro', 'unifuturo', 16);

-- ADMIN DE ORGANIZACIÓN
-- Email: admin@unifuturo.edu
-- Contraseña: 123456789
INSERT INTO users (organization_id, first_name, last_name, email, password_hash, birth_date, account_status)
VALUES (1, 'Ana', 'Directora', 'admin@unifuturo.edu',
        '$2a$10$I0gnxgl3zHLeuDQWurpwce8/rvcwOAjCM0kOlVazBv9WVbrReovq6',
        '1975-03-15', 'ACTIVE');
SET @admin_unifuturo_id = LAST_INSERT_ID();

INSERT INTO user_roles (user_id, role_id) VALUES (@admin_unifuturo_id, 2);
INSERT INTO staff_profiles (user_id, title) VALUES (@admin_unifuturo_id, 'Directora General');

-- PROFESOR
-- Email: profesor@unifuturo.edu
-- Contraseña: 123456789
INSERT INTO users (organization_id, first_name, last_name, email, password_hash, birth_date, account_status)
VALUES (1, 'Carlos', 'Martinez', 'profesor@unifuturo.edu',
        '$2a$10$I0gnxgl3zHLeuDQWurpwce8/rvcwOAjCM0kOlVazBv9WVbrReovq6',
        '1980-07-20', 'ACTIVE');
SET @profesor_unifuturo_id = LAST_INSERT_ID();

INSERT INTO user_roles (user_id, role_id) VALUES (@profesor_unifuturo_id, 4);
INSERT INTO staff_profiles (user_id, employee_id_number, title)
VALUES (@profesor_unifuturo_id, 'EMP-UF-001', 'Profesor de Ingeniería');

-- ESTUDIANTE MAYOR DE EDAD (puede hacer login)
-- Email: estudiante@unifuturo.edu
-- Contraseña: 123456789
INSERT INTO users (organization_id, first_name, last_name, email, password_hash, birth_date, account_status)
VALUES (1, 'Sofia', 'Estudiante', 'estudiante@unifuturo.edu',
        '$2a$10$I0gnxgl3zHLeuDQWurpwce8/rvcwOAjCM0kOlVazBv9WVbrReovq6',
        '2007-05-10', 'ACTIVE'); -- 18 años, mayor que digital_consent_age (16)
SET @estudiante_unifuturo_id = LAST_INSERT_ID();

INSERT INTO user_roles (user_id, role_id) VALUES (@estudiante_unifuturo_id, 5);
INSERT INTO student_profiles (user_id, organization_id, student_id_number, enrollment_date, current_grade_level)
VALUES (@estudiante_unifuturo_id, 1, 'UF-2025-001', '2025-02-01', 'Ingeniería - Semestre 1');

-- =====================================================
-- ORGANIZACIÓN 2: "Colegio Primaria Feliz"
-- =====================================================
INSERT INTO organizations (id, uuid, name, subdomain, digital_consent_age)
VALUES (2, 'org-primaria-002', 'Colegio Primaria Feliz', 'primariafeliz', 18);

-- ADMIN DE ORGANIZACIÓN
-- Email: admin@primariafeliz.edu
-- Contraseña: 123456789
INSERT INTO users (organization_id, first_name, last_name, email, password_hash, birth_date, account_status)
VALUES (2, 'Laura', 'Directora', 'admin@primariafeliz.edu',
        '$2a$10$I0gnxgl3zHLeuDQWurpwce8/rvcwOAjCM0kOlVazBv9WVbrReovq6',
        '1978-09-12', 'ACTIVE');
SET @admin_primaria_id = LAST_INSERT_ID();

INSERT INTO user_roles (user_id, role_id) VALUES (@admin_primaria_id, 2);
INSERT INTO staff_profiles (user_id, title) VALUES (@admin_primaria_id, 'Directora');

-- PROFESOR
-- Email: profesor@primariafeliz.edu
-- Contraseña: 123456789
INSERT INTO users (organization_id, first_name, last_name, email, password_hash, birth_date, account_status)
VALUES (2, 'Roberto', 'Profesor', 'profesor@primariafeliz.edu',
        '$2a$10$I0gnxgl3zHLeuDQWurpwce8/rvcwOAjCM0kOlVazBv9WVbrReovq6',
        '1985-11-03', 'ACTIVE');
SET @profesor_primaria_id = LAST_INSERT_ID();

INSERT INTO user_roles (user_id, role_id) VALUES (@profesor_primaria_id, 4);
INSERT INTO staff_profiles (user_id, title) VALUES (@profesor_primaria_id, 'Profesor 3er Grado');

-- TUTOR/GUARDIAN (puede hacer login)
-- Email: tutor@email.com
-- Contraseña: 123456789
INSERT INTO users (organization_id, first_name, last_name, email, password_hash, birth_date, account_status)
VALUES (2, 'Maria', 'Madre', 'tutor@email.com',
        '$2a$10$I0gnxgl3zHLeuDQWurpwce8/rvcwOAjCM0kOlVazBv9WVbrReovq6',
        '1985-04-18', 'ACTIVE');
SET @tutor_id = LAST_INSERT_ID();

INSERT INTO user_roles (user_id, role_id) VALUES (@tutor_id, 6);
INSERT INTO guardian_profiles (user_id, occupation, is_financial_responsible)
VALUES (@tutor_id, 'Ingeniera', TRUE);

-- ESTUDIANTE MENOR DE EDAD (NO puede hacer login)
-- Email: menor@primariafeliz.edu
-- NO tiene contraseña (password_hash = NULL)
INSERT INTO users (organization_id, first_name, last_name, email, password_hash, birth_date, account_status)
VALUES (2, 'Pedrito', 'Menor', 'menor@primariafeliz.edu', NULL,
        '2016-08-20', 'TUTOR_MANAGED'); -- 9 años, menor que digital_consent_age (18)
SET @menor_id = LAST_INSERT_ID();

INSERT INTO user_roles (user_id, role_id) VALUES (@menor_id, 5);
INSERT INTO student_profiles (user_id, organization_id, student_id_number, enrollment_date, current_grade_level)
VALUES (@menor_id, 2, 'PF-2025-001', '2025-02-01', '3er Grado');

-- Relación tutor-estudiante
INSERT INTO student_guardian_relationships (student_user_id, guardian_user_id, relationship_type, is_primary_contact)
VALUES (@menor_id, @tutor_id, 'Madre', TRUE);

-- =====================================================
-- ESTRUCTURA ACADÉMICA DE EJEMPLO
-- =====================================================

-- Materias para Universidad del Futuro
INSERT INTO subjects (organization_id, name, subject_code, grade_level)
VALUES (1, 'Programación I', 'PROG-I', 'Ingeniería - Semestre 1');
SET @subject_prog_id = LAST_INSERT_ID();

-- Período académico
INSERT INTO academic_terms (organization_id, name, start_date, end_date)
VALUES (1, 'Semestre 2025-1', '2025-02-01', '2025-06-30');
SET @term_unifuturo_id = LAST_INSERT_ID();

-- Curso
INSERT INTO courses (subject_id, academic_term_id, teacher_user_id, course_code)
VALUES (@subject_prog_id, @term_unifuturo_id, @profesor_unifuturo_id, 'PROG-I-2025-1');
SET @course_prog_id = LAST_INSERT_ID();

-- Inscripción del estudiante
INSERT INTO enrollments (student_user_id, course_id, status)
VALUES (@estudiante_unifuturo_id, @course_prog_id, 'ACTIVE');

-- =====================================================
-- VERIFICACIONES Y ESTADÍSTICAS
-- =====================================================

-- Mostrar resumen de usuarios creados
SELECT
    'Usuarios creados' as tipo,
    COUNT(*) as cantidad
FROM users
UNION ALL
SELECT
    'Organizaciones creadas' as tipo,
    COUNT(*) as cantidad
FROM organizations
UNION ALL
SELECT
    'Emails en índice' as tipo,
    COUNT(*) as cantidad
FROM email_organization_index;

-- =====================================================
-- DOCUMENTACIÓN DE CREDENCIALES
-- =====================================================

/*
CREDENCIALES PARA LOGIN:

SUPER ADMIN:
- Email: superadmin@plataforma.com
- Contraseña: 123456789

UNIVERSIDAD DEL FUTURO (unifuturo):
- Admin: admin@unifuturo.edu / 123456789
- Profesor: profesor@unifuturo.edu / 123456789
- Estudiante: estudiante@unifuturo.edu / 123456789

COLEGIO PRIMARIA FELIZ (primariafeliz):
- Admin: admin@primariafeliz.edu / 123456789
- Profesor: profesor@primariafeliz.edu / 123456789
- Tutor: tutor@email.com / 123456789
- Menor: menor@primariafeliz.edu / NO PUEDE HACER LOGIN (TUTOR_MANAGED)

HASH USADO:
- $2a$10$I0gnxgl3zHLeuDQWurpwce8/rvcwOAjCM0kOlVazBv9WVbrReovq6
*/