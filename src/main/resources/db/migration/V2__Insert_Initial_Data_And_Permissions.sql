-- =================================================================
-- MIGRACIÓN V2: DATOS INICIALES Y SISTEMA DE PERMISOS
-- Incluye roles, permisos y datos de prueba funcionales
-- =================================================================

-- -----------------------------------------------------
-- ROLES DEL SISTEMA (Todos los roles necesarios)
-- -----------------------------------------------------
INSERT INTO roles (id, name, scope) VALUES
                                        (1, 'SYSTEM_ADMIN', 'SYSTEM'),
                                        (2, 'ORGANIZATION_ADMIN', 'TENANT'),
                                        (3, 'ACADEMIC_DIRECTOR', 'TENANT'),
                                        (4, 'TEACHER', 'TENANT'),
                                        (5, 'STUDENT', 'TENANT'),
                                        (6, 'GUARDIAN', 'TENANT'),
                                        (7, 'REGISTRAR', 'TENANT'),
                                        (8, 'ACADEMIC_COORDINATOR', 'TENANT'),
                                        (9, 'CURRICULUM_MANAGER', 'TENANT'),
                                        (10, 'ENROLLMENT_MANAGER', 'TENANT'),
                                        (11, 'ACADEMIC_ADVISOR', 'TENANT'),
                                        (12, 'ACADEMIC_SECRETARY', 'TENANT');

-- -----------------------------------------------------
-- PERMISOS DEL SISTEMA (Todos los permisos necesarios)
-- -----------------------------------------------------
INSERT INTO permissions (name, description, category) VALUES
-- Permisos de Sistema
('system:admin', 'Administración completa del sistema', 'system'),
('system:monitor', 'Monitoreo del sistema', 'system'),
('system:backup', 'Realizar respaldos del sistema', 'system'),

-- Permisos de Organización
('organization:create', 'Crear nuevas organizaciones', 'organization'),
('organization:read', 'Ver información de organizaciones', 'organization'),
('organization:update', 'Actualizar configuración de organizaciones', 'organization'),
('organization:delete', 'Eliminar organizaciones', 'organization'),
('organization:manage_users', 'Gestionar usuarios de la organización', 'organization'),
('organization:view_reports', 'Ver reportes de la organización', 'organization'),

-- Permisos de Usuarios
('user:create', 'Crear nuevos usuarios', 'user'),
('user:read', 'Ver información de usuarios', 'user'),
('user:update', 'Actualizar información de usuarios', 'user'),
('user:delete', 'Eliminar usuarios', 'user'),
('user:change_password', 'Cambiar contraseñas de usuarios', 'user'),
('user:suspend', 'Suspender/reactivar usuarios', 'user'),
('user:assign_roles', 'Asignar roles a usuarios', 'user'),

-- Permisos Académicos - Gestión Curricular
('curriculum:create', 'Crear planes de estudio', 'academic'),
('curriculum:read', 'Ver planes de estudio', 'academic'),
('curriculum:update', 'Actualizar planes de estudio', 'academic'),
('curriculum:delete', 'Eliminar planes de estudio', 'academic'),
('curriculum:approve', 'Aprobar cambios curriculares', 'academic'),

-- Permisos Académicos - Cursos
('course:create', 'Crear cursos', 'academic'),
('course:read', 'Ver cursos', 'academic'),
('course:update', 'Actualizar información de cursos', 'academic'),
('course:delete', 'Eliminar cursos', 'academic'),
('course:assign_teacher', 'Asignar docentes a cursos', 'academic'),
('course:manage_enrollment', 'Gestionar inscripciones', 'academic'),

-- Permisos Académicos - Estudiantes
('student:create', 'Registrar nuevos estudiantes', 'academic'),
('student:read', 'Ver información de estudiantes', 'academic'),
('student:update', 'Actualizar información de estudiantes', 'academic'),
('student:delete', 'Eliminar estudiantes', 'academic'),
('student:view_grades', 'Ver calificaciones de estudiantes', 'academic'),
('student:manage_grades', 'Gestionar calificaciones', 'academic'),
('student:view_attendance', 'Ver asistencia de estudiantes', 'academic'),
('student:manage_attendance', 'Gestionar asistencia', 'academic'),

-- Permisos Académicos - Docentes
('teacher:create', 'Registrar nuevos docentes', 'academic'),
('teacher:read', 'Ver información de docentes', 'academic'),
('teacher:update', 'Actualizar información de docentes', 'academic'),
('teacher:delete', 'Eliminar docentes', 'academic'),
('teacher:assign_courses', 'Asignar cursos a docentes', 'academic'),
('teacher:view_schedule', 'Ver horarios de docentes', 'academic'),
('teacher:manage_schedule', 'Gestionar horarios', 'academic'),

-- Permisos de Registro Académico
('registry:view_transcripts', 'Ver expedientes académicos', 'registry'),
('registry:issue_certificates', 'Emitir certificados', 'registry'),
('registry:manage_grades', 'Gestionar calificaciones oficiales', 'registry'),
('registry:validate_credits', 'Validar créditos académicos', 'registry'),
('registry:generate_reports', 'Generar reportes académicos', 'registry'),

-- Permisos de Reportes
('reports:academic', 'Generar reportes académicos', 'reports'),
('reports:financial', 'Generar reportes financieros', 'reports'),
('reports:administrative', 'Generar reportes administrativos', 'reports'),
('reports:export', 'Exportar reportes', 'reports'),

-- Permisos de Configuración
('config:system', 'Configurar parámetros del sistema', 'config'),
('config:academic', 'Configurar parámetros académicos', 'config'),
('config:notifications', 'Configurar notificaciones', 'config'),

-- Permisos para SUBJECTS (Materias)
('ACADEMIC_SUBJECT_VIEW', 'Ver materias y sus detalles', 'ACADEMIC'),
('ACADEMIC_SUBJECT_LIST', 'Listar materias de la organización', 'ACADEMIC'),
('ACADEMIC_SUBJECT_CREATE', 'Crear nuevas materias', 'ACADEMIC'),
('ACADEMIC_SUBJECT_UPDATE', 'Actualizar información de materias existentes', 'ACADEMIC'),
('ACADEMIC_SUBJECT_DELETE', 'Eliminar materias (solo si no están en uso)', 'ACADEMIC'),
('ACADEMIC_SUBJECT_ACTIVATE', 'Activar materias desactivadas', 'ACADEMIC'),
('ACADEMIC_SUBJECT_DEACTIVATE', 'Desactivar materias activas', 'ACADEMIC'),
('ACADEMIC_SUBJECT_MANAGE_PREREQUISITES', 'Gestionar prerrequisitos entre materias', 'ACADEMIC'),
('ACADEMIC_SUBJECT_REPORTS', 'Generar reportes académicos de materias', 'ACADEMIC'),

-- Permisos para ACADEMIC TERMS (Períodos Académicos)
('ACADEMIC_TERM_VIEW', 'Ver términos académicos y sus detalles', 'ACADEMIC'),
('ACADEMIC_TERM_LIST', 'Listar términos académicos de la organización', 'ACADEMIC'),
('ACADEMIC_TERM_CREATE', 'Crear nuevos términos académicos', 'ACADEMIC'),
('ACADEMIC_TERM_UPDATE', 'Actualizar información de términos académicos', 'ACADEMIC'),
('ACADEMIC_TERM_DELETE', 'Eliminar términos académicos (solo si no están en uso)', 'ACADEMIC'),
('ACADEMIC_TERM_MANAGE', 'Gestión completa de términos (iniciar, finalizar, marcar como actual)', 'ACADEMIC'),
('ACADEMIC_TERM_SET_CURRENT', 'Establecer término académico como actual', 'ACADEMIC'),
('ACADEMIC_TERM_START', 'Iniciar término académico', 'ACADEMIC'),
('ACADEMIC_TERM_END', 'Finalizar término académico', 'ACADEMIC'),
('ACADEMIC_TERM_UPDATE_DATES', 'Modificar fechas de términos académicos', 'ACADEMIC'),
('ACADEMIC_TERM_REPORTS', 'Generar reportes de términos académicos', 'ACADEMIC'),
('ACADEMIC_TERM_STATISTICS', 'Ver estadísticas de términos académicos', 'ACADEMIC'),

-- Permisos de Matrícula
('enrollment:create', 'Crear nuevas matrículas', 'enrollment'),
('enrollment:read', 'Ver información de matrículas', 'enrollment'),
('enrollment:update', 'Actualizar matrículas', 'enrollment'),
('enrollment:delete', 'Cancelar matrículas', 'enrollment'),
('enrollment:approve', 'Aprobar matrículas', 'enrollment'),
('enrollment:manage_periods', 'Gestionar períodos de matrícula', 'enrollment'),

-- Permisos de Asesoría Académica
('advising:create_sessions', 'Crear sesiones de asesoría', 'advising'),
('advising:view_sessions', 'Ver sesiones de asesoría', 'advising'),
('advising:manage_appointments', 'Gestionar citas de asesoría', 'advising'),
('advising:generate_reports', 'Generar reportes de asesoría', 'advising'),

-- Permisos de Coordinación
('coordination:schedule_management', 'Gestión de horarios', 'coordination'),
('coordination:resource_allocation', 'Asignación de recursos', 'coordination'),
('coordination:faculty_coordination', 'Coordinación de facultad', 'coordination'),

-- Permisos de Validación
('validation:credit_transfer', 'Validar transferencia de créditos', 'validation'),
('validation:degree_requirements', 'Validar requisitos de grado', 'validation'),
('validation:academic_records', 'Validar registros académicos', 'validation'),

-- Permisos generales del módulo académico
('ACADEMIC_MODULE_ADMIN', 'Administración completa del módulo académico', 'ACADEMIC'),
('ACADEMIC_CALENDAR_VIEW', 'Ver calendario académico', 'ACADEMIC'),
('ACADEMIC_CALENDAR_MANAGE', 'Gestionar calendario académico', 'ACADEMIC');

-- -----------------------------------------------------
-- ASIGNACIÓN DE PERMISOS A ROLES
-- -----------------------------------------------------

-- SYSTEM_ADMIN: Todos los permisos del sistema
INSERT INTO role_permissions (role_id, permission_id)
SELECT 1, id FROM permissions;

-- ORGANIZATION_ADMIN: Permisos administrativos de organización
INSERT INTO role_permissions (role_id, permission_id)
SELECT 2, id FROM permissions
WHERE category IN ('organization', 'user', 'academic', 'registry', 'reports', 'config', 'ACADEMIC', 'enrollment')
  AND name NOT IN ('organization:create', 'organization:delete');

-- ACADEMIC_DIRECTOR: Permisos académicos y curriculares
INSERT INTO role_permissions (role_id, permission_id)
SELECT 3, id FROM permissions
WHERE category IN ('academic', 'registry', 'ACADEMIC', 'coordination')
   OR name IN ('user:read', 'user:update', 'reports:academic', 'config:academic');

-- TEACHER: Permisos básicos de docencia
INSERT INTO role_permissions (role_id, permission_id)
SELECT 4, id FROM permissions
WHERE name IN (
               'course:read',
               'student:read', 'student:view_grades', 'student:manage_grades',
               'student:view_attendance', 'student:manage_attendance',
               'teacher:view_schedule',
               'reports:academic',
               'ACADEMIC_SUBJECT_VIEW', 'ACADEMIC_SUBJECT_LIST',
               'ACADEMIC_TERM_VIEW', 'ACADEMIC_TERM_LIST',
               'ACADEMIC_CALENDAR_VIEW'
    );

-- STUDENT: Permisos de consulta básicos
INSERT INTO role_permissions (role_id, permission_id)
SELECT 5, id FROM permissions
WHERE name IN (
               'course:read',
               'student:read',
               'ACADEMIC_SUBJECT_VIEW', 'ACADEMIC_SUBJECT_LIST',
               'ACADEMIC_TERM_VIEW', 'ACADEMIC_CALENDAR_VIEW'
    );

-- GUARDIAN: Permisos para tutores
INSERT INTO role_permissions (role_id, permission_id)
SELECT 6, id FROM permissions
WHERE name IN (
               'student:read', 'student:view_grades', 'student:view_attendance'
    );

-- REGISTRAR: Gestión completa de registros académicos
INSERT INTO role_permissions (role_id, permission_id)
SELECT 7, id FROM permissions
WHERE category = 'registry'
   OR name IN (
               'student:read', 'student:view_grades', 'student:manage_grades',
               'course:read', 'teacher:read',
               'validation:credit_transfer', 'validation:degree_requirements', 'validation:academic_records',
               'reports:academic'
    );

-- ACADEMIC_COORDINATOR: Coordinación académica y curricular
INSERT INTO role_permissions (role_id, permission_id)
SELECT 8, id FROM permissions
WHERE category IN ('coordination', 'academic', 'ACADEMIC')
   OR name IN (
               'curriculum:read', 'curriculum:update',
               'course:read', 'course:update', 'course:assign_teacher',
               'teacher:read', 'teacher:assign_courses', 'teacher:view_schedule', 'teacher:manage_schedule',
               'student:read', 'student:view_grades',
               'reports:academic', 'config:academic'
    );

-- CURRICULUM_MANAGER: Gestión especializada de currículos
INSERT INTO role_permissions (role_id, permission_id)
SELECT 9, id FROM permissions
WHERE category = 'academic' AND name LIKE 'curriculum:%'
   OR name IN (
               'course:create', 'course:read', 'course:update', 'course:delete',
               'config:academic', 'reports:academic',
               'ACADEMIC_SUBJECT_CREATE', 'ACADEMIC_SUBJECT_UPDATE', 'ACADEMIC_SUBJECT_VIEW', 'ACADEMIC_SUBJECT_LIST'
    );

-- ENROLLMENT_MANAGER: Gestión de matrículas y períodos
INSERT INTO role_permissions (role_id, permission_id)
SELECT 10, id FROM permissions
WHERE category = 'enrollment'
   OR name IN (
               'student:create', 'student:read', 'student:update',
               'course:read', 'course:manage_enrollment',
               'reports:academic'
    );

-- ACADEMIC_ADVISOR: Asesoría académica a estudiantes
INSERT INTO role_permissions (role_id, permission_id)
SELECT 11, id FROM permissions
WHERE category = 'advising'
   OR name IN (
               'student:read', 'student:view_grades', 'student:view_attendance',
               'course:read', 'curriculum:read',
               'reports:academic'
    );

-- ACADEMIC_SECRETARY: Permisos administrativos académicos
INSERT INTO role_permissions (role_id, permission_id)
SELECT 12, id FROM permissions
WHERE name IN (
               'ACADEMIC_SUBJECT_VIEW', 'ACADEMIC_SUBJECT_LIST', 'ACADEMIC_SUBJECT_CREATE',
               'ACADEMIC_SUBJECT_UPDATE', 'ACADEMIC_SUBJECT_REPORTS',
               'ACADEMIC_TERM_VIEW', 'ACADEMIC_TERM_LIST', 'ACADEMIC_TERM_REPORTS',
               'ACADEMIC_TERM_STATISTICS', 'ACADEMIC_CALENDAR_VIEW',
               'student:read', 'student:update', 'teacher:read', 'course:read',
               'reports:academic'
    );

-- -----------------------------------------------------
-- DATOS DE PRUEBA
-- -----------------------------------------------------

-- SUPER ADMIN (Sistema)
-- Email: superadmin@plataforma.com
-- Contraseña: SecureAdm!n2024$
-- Hash: $2a$12$IZ70/dvg1W8lJGjK5xADcO/fRkMgtdmToXe7HVWT5UWD4ChEJ.u92
INSERT INTO users (id, organization_id, first_name, last_name, email, password_hash, birth_date, account_status)
VALUES (UUID(), NULL, 'Super', 'Admin', 'superadmin@plataforma.com',
        '$2a$12$IZ70/dvg1W8lJGjK5xADcO/fRkMgtdmToXe7HVWT5UWD4ChEJ.u92',
        '1985-01-01', 'ACTIVE');

-- Obtener el ID del super admin para asignar rol
SET @super_admin_id = (SELECT id FROM users WHERE email = 'superadmin@plataforma.com');
INSERT INTO user_roles (user_id, role_id) VALUES (@super_admin_id, 1);

-- =====================================================
-- ORGANIZACIÓN 1: "Universidad del Futuro"
-- =====================================================
INSERT INTO organizations (id, name, subdomain, digital_consent_age)
VALUES (UUID(), 'Universidad del Futuro', 'unifuturo', 16);

SET @org_unifuturo_id = (SELECT id FROM organizations WHERE subdomain = 'unifuturo');

-- ADMIN DE ORGANIZACIÓN
-- Email: admin@unifuturo.edu
-- Contraseña: AdminUni#2024!
INSERT INTO users (id, organization_id, first_name, last_name, email, password_hash, birth_date, account_status)
VALUES (UUID(), @org_unifuturo_id, 'Ana', 'Directora', 'admin@unifuturo.edu',
        '$2a$12$bpOrg4RtA4rPysVEkKdXduvhUsGPZDH6mAJxwu4oOr0wA4kcYRDmO',
        '1975-03-15', 'ACTIVE');

SET @admin_unifuturo_id = (SELECT id FROM users WHERE email = 'admin@unifuturo.edu');
INSERT INTO user_roles (user_id, role_id) VALUES (@admin_unifuturo_id, 2);
INSERT INTO staff_profiles (user_id, title) VALUES (@admin_unifuturo_id, 'Directora General');

-- PROFESOR
-- Email: profesor@unifuturo.edu
-- Contraseña: TeachUni@2024
INSERT INTO users (id, organization_id, first_name, last_name, email, password_hash, birth_date, account_status)
VALUES (UUID(), @org_unifuturo_id, 'Carlos', 'Martinez', 'profesor@unifuturo.edu',
        '$2a$12$VRxH1LzOK6crX5mje6Z0IuJJxF7/n.g6SYFHFKShYO7/vlThF/H5.',
        '1980-07-20', 'ACTIVE');

SET @profesor_unifuturo_id = (SELECT id FROM users WHERE email = 'profesor@unifuturo.edu');
INSERT INTO user_roles (user_id, role_id) VALUES (@profesor_unifuturo_id, 4);
INSERT INTO staff_profiles (user_id, employee_id_number, title)
VALUES (@profesor_unifuturo_id, 'EMP-UF-001', 'Profesor de Ingeniería');

-- ESTUDIANTE MAYOR DE EDAD (puede hacer login)
-- Email: estudiante@unifuturo.edu
-- Contraseña: StudUni$2024
INSERT INTO users (id, organization_id, first_name, last_name, email, password_hash, birth_date, account_status)
VALUES (UUID(), @org_unifuturo_id, 'Sofia', 'Estudiante', 'estudiante@unifuturo.edu',
        '$2a$12$n1vOgQdREUYZi2SCX5um7uMC0Rp/utdRWbFBFO6b5g0qi.iT3LKFO',
        '2007-05-10', 'ACTIVE'); -- 18 años, mayor que digital_consent_age (16)

SET @estudiante_unifuturo_id = (SELECT id FROM users WHERE email = 'estudiante@unifuturo.edu');
INSERT INTO user_roles (user_id, role_id) VALUES (@estudiante_unifuturo_id, 5);
INSERT INTO student_profiles (user_id, organization_id, student_id_number, enrollment_date, current_grade_level)
VALUES (@estudiante_unifuturo_id, @org_unifuturo_id, 'UF-2025-001', '2025-02-01', 'Ingeniería - Semestre 1');

-- =====================================================
-- ORGANIZACIÓN 2: "Colegio Primaria Feliz"
-- =====================================================
INSERT INTO organizations (id, name, subdomain, digital_consent_age)
VALUES (UUID(), 'Colegio Primaria Feliz', 'primariafeliz', 18);

SET @org_primaria_id = (SELECT id FROM organizations WHERE subdomain = 'primariafeliz');

-- ADMIN DE ORGANIZACIÓN
-- Email: admin@primariafeliz.edu
-- Contraseña: AdminPrim#2024
INSERT INTO users (id, organization_id, first_name, last_name, email, password_hash, birth_date, account_status)
VALUES (UUID(), @org_primaria_id, 'Laura', 'Directora', 'admin@primariafeliz.edu',
        '$2a$12$H7CZTR.GCKi6YvxbO3jo7uM9zNYcn2QojMAOhUVYtbzhx1iShtp7.',
        '1978-09-12', 'ACTIVE');

SET @admin_primaria_id = (SELECT id FROM users WHERE email = 'admin@primariafeliz.edu');
INSERT INTO user_roles (user_id, role_id) VALUES (@admin_primaria_id, 2);
INSERT INTO staff_profiles (user_id, title) VALUES (@admin_primaria_id, 'Directora');

-- PROFESOR
-- Email: profesor@primariafeliz.edu
-- Contraseña: TeachPrim@2024
INSERT INTO users (id, organization_id, first_name, last_name, email, password_hash, birth_date, account_status)
VALUES (UUID(), @org_primaria_id, 'Roberto', 'Profesor', 'profesor@primariafeliz.edu',
        '$2a$12$UjUS237e9usnxgoxLiOm8eUVp/0t8MbLmUClNuEc8Cn2EhVDQZpeC',
        '1985-11-03', 'ACTIVE');

SET @profesor_primaria_id = (SELECT id FROM users WHERE email = 'profesor@primariafeliz.edu');
INSERT INTO user_roles (user_id, role_id) VALUES (@profesor_primaria_id, 4);
INSERT INTO staff_profiles (user_id, title) VALUES (@profesor_primaria_id, 'Profesor 3er Grado');

-- TUTOR/GUARDIAN (puede hacer login)
-- Email: tutor@email.com
-- Contraseña: TutorGuard!2024
INSERT INTO users (id, organization_id, first_name, last_name, email, password_hash, birth_date, account_status)
VALUES (UUID(), @org_primaria_id, 'Maria', 'Madre', 'tutor@email.com',
        '$2a$12$k5HRkyBMJ591i/5xinrAn./fo3zS2RMKKsw2NnE6tgIZ36h3A3u.G',
        '1985-04-18', 'ACTIVE');

SET @tutor_id = (SELECT id FROM users WHERE email = 'tutor@email.com');
INSERT INTO user_roles (user_id, role_id) VALUES (@tutor_id, 6);
INSERT INTO guardian_profiles (user_id, occupation, is_financial_responsible)
VALUES (@tutor_id, 'Ingeniera', TRUE);

-- ESTUDIANTE MENOR DE EDAD (NO puede hacer login)
-- Email: menor@primariafeliz.edu
-- NO tiene contraseña (password_hash = NULL)
INSERT INTO users (id, organization_id, first_name, last_name, email, password_hash, birth_date, account_status)
VALUES (UUID(), @org_primaria_id, 'Pedrito', 'Menor', 'menor@primariafeliz.edu', NULL,
        '2016-08-20', 'TUTOR_MANAGED'); -- 9 años, menor que digital_consent_age (18)

SET @menor_id = (SELECT id FROM users WHERE email = 'menor@primariafeliz.edu');
INSERT INTO user_roles (user_id, role_id) VALUES (@menor_id, 5);
INSERT INTO student_profiles (user_id, organization_id, student_id_number, enrollment_date, current_grade_level)
VALUES (@menor_id, @org_primaria_id, 'PF-2025-001', '2025-02-01', '3er Grado');

-- Relación tutor-estudiante
INSERT INTO student_guardian_relationships (id, student_user_id, guardian_user_id, relationship_type, is_primary_contact)
VALUES (UUID(), @menor_id, @tutor_id, 'Madre', TRUE);

-- =====================================================
-- ESTRUCTURA ACADÉMICA DE EJEMPLO
-- =====================================================

-- Materias para Universidad del Futuro
INSERT INTO subjects (id, organization_id, name, subject_code, credits, grade_level)
VALUES (UUID(), @org_unifuturo_id, 'Programación I', 'PROG-I', 4, 'Ingeniería - Semestre 1');
SET @subject_prog_id = (SELECT id FROM subjects WHERE subject_code = 'PROG-I' AND organization_id = @org_unifuturo_id);

-- Período académico
INSERT INTO academic_terms (id, organization_id, name, start_date, end_date, is_current_term)
VALUES (UUID(), @org_unifuturo_id, 'Semestre 2025-1', '2025-02-01', '2025-06-30', TRUE);
SET @term_unifuturo_id = (SELECT id FROM academic_terms WHERE name = 'Semestre 2025-1' AND organization_id = @org_unifuturo_id);

-- Curso
INSERT INTO courses (id, subject_id, academic_term_id, teacher_user_id, course_code)
VALUES (UUID(), @subject_prog_id, @term_unifuturo_id, @profesor_unifuturo_id, 'PROG-I-2025-1');
SET @course_prog_id = (SELECT id FROM courses WHERE course_code = 'PROG-I-2025-1');

-- Inscripción del estudiante
INSERT INTO enrollments (id, student_user_id, course_id, status)
VALUES (UUID(), @estudiante_unifuturo_id, @course_prog_id, 'ACTIVE');

-- =====================================================
-- CONFIGURACIONES DE SEGURIDAD
-- =====================================================

-- Configuración de seguridad por defecto para las organizaciones
INSERT INTO organization_security_config (id, organization_id, password_min_length, require_2fa) VALUES
                                                                                                     (UUID(), @org_unifuturo_id, 12, FALSE), -- Universidad del Futuro
                                                                                                     (UUID(), @org_primaria_id, 10, FALSE);  -- Colegio Primaria Feliz

-- Registrar cambio de contraseñas en auditoría (acción de migración)
INSERT INTO password_audit_log (id, user_id, action)
SELECT UUID(), id, 'CHANGED' FROM users
WHERE email IN (
                'superadmin@plataforma.com',
                'admin@unifuturo.edu',
                'profesor@unifuturo.edu',
                'estudiante@unifuturo.edu',
                'admin@primariafeliz.edu',
                'profesor@primariafeliz.edu',
                'tutor@email.com'
    );

-- =====================================================
-- VERIFICACIONES Y ESTADÍSTICAS
-- =====================================================

-- Mostrar resumen de datos creados
SELECT 'Usuarios creados' as tipo, COUNT(*) as cantidad FROM users
UNION ALL
SELECT 'Organizaciones creadas' as tipo, COUNT(*) as cantidad FROM organizations
UNION ALL
SELECT 'Roles creados' as tipo, COUNT(*) as cantidad FROM roles
UNION ALL
SELECT 'Permisos creados' as tipo, COUNT(*) as cantidad FROM permissions
UNION ALL
SELECT 'Asignaciones rol-permiso' as tipo, COUNT(*) as cantidad FROM role_permissions
UNION ALL
SELECT 'Materias creadas' as tipo, COUNT(*) as cantidad FROM subjects
UNION ALL
SELECT 'Períodos académicos' as tipo, COUNT(*) as cantidad FROM academic_terms
UNION ALL
SELECT 'Cursos creados' as tipo, COUNT(*) as cantidad FROM courses
UNION ALL
SELECT 'Inscripciones' as tipo, COUNT(*) as cantidad FROM enrollments;