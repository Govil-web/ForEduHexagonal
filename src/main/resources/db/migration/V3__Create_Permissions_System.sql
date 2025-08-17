-- =================================================================
-- MIGRACIÓN V3: SISTEMA DE PERMISOS GRANULAR
-- Implementa la gestión de permisos para roles del sistema académico
-- =================================================================

-- -----------------------------------------------------
-- Tabla: permissions (Permisos granulares del sistema)
-- -----------------------------------------------------
CREATE TABLE permissions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    category VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    
    INDEX idx_permissions_category (category)
);

-- -----------------------------------------------------
-- Tabla: role_permissions (Relación roles-permisos)
-- -----------------------------------------------------
CREATE TABLE role_permissions (
    role_id INT NOT NULL,
    permission_id INT NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- PERMISOS CORE DEL SISTEMA
-- -----------------------------------------------------

-- Permisos de Sistema
INSERT INTO permissions (name, description, category) VALUES
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
('config:notifications', 'Configurar notificaciones', 'config');

-- -----------------------------------------------------
-- ASIGNACIÓN DE PERMISOS A ROLES EXISTENTES
-- -----------------------------------------------------

-- SYSTEM_ADMIN: Todos los permisos del sistema
INSERT INTO role_permissions (role_id, permission_id) 
SELECT 1, id FROM permissions;

-- ORGANIZATION_ADMIN: Permisos administrativos de organización
INSERT INTO role_permissions (role_id, permission_id) 
SELECT 2, id FROM permissions 
WHERE category IN ('organization', 'user', 'academic', 'registry', 'reports', 'config')
AND name NOT IN ('organization:create', 'organization:delete');

-- ACADEMIC_DIRECTOR: Permisos académicos y curriculares
INSERT INTO role_permissions (role_id, permission_id) 
SELECT 3, id FROM permissions 
WHERE category IN ('academic', 'registry') 
OR name IN ('user:read', 'user:update', 'reports:academic', 'config:academic');

-- TEACHER: Permisos básicos de docencia
INSERT INTO role_permissions (role_id, permission_id) 
SELECT 4, id FROM permissions 
WHERE name IN (
    'course:read',
    'student:read', 'student:view_grades', 'student:manage_grades',
    'student:view_attendance', 'student:manage_attendance',
    'teacher:view_schedule',
    'reports:academic'
);

-- STUDENT: Permisos de consulta básicos
INSERT INTO role_permissions (role_id, permission_id) 
SELECT 5, id FROM permissions 
WHERE name IN (
    'course:read',
    'student:read' -- Solo su propia información
);

-- GUARDIAN: Permisos para tutores
INSERT INTO role_permissions (role_id, permission_id) 
SELECT 6, id FROM permissions 
WHERE name IN (
    'student:read', 'student:view_grades', 'student:view_attendance'
);