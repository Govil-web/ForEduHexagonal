-- =================================================================
-- MIGRACIÓN V4: ROLES ADICIONALES PARA GESTIÓN EDUCATIVA
-- Agrega roles específicos para la gestión educativa institucional
-- =================================================================

-- -----------------------------------------------------
-- NUEVOS ROLES PARA GESTIÓN EDUCATIVA
-- -----------------------------------------------------
INSERT INTO roles (id, name, scope) VALUES
(7, 'REGISTRAR', 'TENANT'),           -- Registrador académico
(8, 'ACADEMIC_COORDINATOR', 'TENANT'), -- Coordinador académico
(9, 'CURRICULUM_MANAGER', 'TENANT'),   -- Gestor curricular
(10, 'ENROLLMENT_MANAGER', 'TENANT'),  -- Gestor de matrículas
(11, 'ACADEMIC_ADVISOR', 'TENANT');    -- Asesor académico

-- -----------------------------------------------------
-- PERMISOS ADICIONALES PARA GESTIÓN EDUCATIVA
-- -----------------------------------------------------
INSERT INTO permissions (name, description, category) VALUES
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
('validation:academic_records', 'Validar registros académicos', 'validation');

-- -----------------------------------------------------
-- ASIGNACIÓN DE PERMISOS A NUEVOS ROLES
-- -----------------------------------------------------

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
WHERE category IN ('coordination', 'academic')
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
    'config:academic', 'reports:academic'
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