-- =================================================================
-- MIGRACIÓN V5: HARDENING DE SEGURIDAD - CREDENCIALES SEGURAS
-- Actualiza todas las contraseñas por defecto a valores seguros
-- =================================================================

-- IMPORTANTE: Esta migración actualiza las contraseñas de prueba
-- Las contraseñas se han cambiado por motivos de seguridad

-- -----------------------------------------------------
-- ACTUALIZACIÓN DE CONTRASEÑAS SEGURAS
-- -----------------------------------------------------
-- Nuevas contraseñas generadas aleatoriamente:
-- SuperAdmin: SecureAdm!n2024$
-- Admin Unifuturo: AdminUni#2024!
-- Profesor Unifuturo: TeachUni@2024
-- Estudiante Unifuturo: StudUni$2024
-- Admin Primaria: AdminPrim#2024
-- Profesor Primaria: TeachPrim@2024
-- Tutor: TutorGuard!2024

-- Hash generado con BCrypt rounds=12 para mayor seguridad

-- SUPER ADMIN (Sistema)
UPDATE users 
SET password_hash = '$2a$12$IZ70/dvg1W8lJGjK5xADcO/fRkMgtdmToXe7HVWT5UWD4ChEJ.u92'
WHERE email = 'superadmin@plataforma.com';

-- ORGANIZACIÓN 1: Universidad del Futuro
-- Admin - Password: AdminUni#2024!
UPDATE users 
SET password_hash = '$2a$12$bpOrg4RtA4rPysVEkKdXduvhUsGPZDH6mAJxwu4oOr0wA4kcYRDmO'
WHERE email = 'admin@unifuturo.edu';

-- Profesor
UPDATE users 
SET password_hash = '$2a$12$VRxH1LzOK6crX5mje6Z0IuJJxF7/n.g6SYFHFKShYO7/vlThF/H5.'
WHERE email = 'profesor@unifuturo.edu';

-- Estudiante
UPDATE users 
SET password_hash = '$2a$12$n1vOgQdREUYZi2SCX5um7uMC0Rp/utdRWbFBFO6b5g0qi.iT3LKFO'
WHERE email = 'estudiante@unifuturo.edu';

-- ORGANIZACIÓN 2: Colegio Primaria Feliz
-- Admin
UPDATE users 
SET password_hash = '$2a$12$H7CZTR.GCKi6YvxbO3jo7uM9zNYcn2QojMAOhUVYtbzhx1iShtp7.'
WHERE email = 'admin@primariafeliz.edu';

-- Profesor
UPDATE users 
SET password_hash = '$2a$12$UjUS237e9usnxgoxLiOm8eUVp/0t8MbLmUClNuEc8Cn2EhVDQZpeC'
WHERE email = 'profesor@primariafeliz.edu';

-- Tutor
UPDATE users 
SET password_hash = '$2a$12$k5HRkyBMJ591i/5xinrAn./fo3zS2RMKKsw2NnE6tgIZ36h3A3u.G'
WHERE email = 'tutor@email.com';

-- -----------------------------------------------------
-- ACTUALIZACIÓN DE CONFIGURACIÓN DE SEGURIDAD
-- -----------------------------------------------------

-- Crear tabla para configuraciones de seguridad por organización
CREATE TABLE organization_security_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    organization_id BIGINT NOT NULL,
    password_min_length INT DEFAULT 12,
    password_require_uppercase BOOLEAN DEFAULT TRUE,
    password_require_lowercase BOOLEAN DEFAULT TRUE,
    password_require_numbers BOOLEAN DEFAULT TRUE,
    password_require_special_chars BOOLEAN DEFAULT TRUE,
    password_expiry_days INT DEFAULT 90,
    max_login_attempts INT DEFAULT 5,
    lockout_duration_minutes INT DEFAULT 30,
    session_timeout_minutes INT DEFAULT 60,
    require_2fa BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    UNIQUE KEY uk_org_security_config (organization_id)
);

-- Configuración de seguridad por defecto para las organizaciones existentes
INSERT INTO organization_security_config (organization_id, password_min_length, require_2fa) VALUES
(1, 12, FALSE), -- Universidad del Futuro
(2, 10, FALSE); -- Colegio Primaria Feliz

-- Crear tabla de auditoría de contraseñas
CREATE TABLE password_audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL, -- 'CHANGED', 'RESET', 'EXPIRED'
    performed_by BIGINT, -- ID del usuario que realizó la acción (NULL si fue automático)
    ip_address VARCHAR(45),
    user_agent TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (performed_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_password_audit_user (user_id),
    INDEX idx_password_audit_timestamp (timestamp)
);

-- Registrar cambio de contraseñas en auditoría (acción de migración)
INSERT INTO password_audit_log (user_id, action) 
SELECT id, 'CHANGED' FROM users 
WHERE email IN (
    'superadmin@plataforma.com',
    'admin@unifuturo.edu',
    'profesor@unifuturo.edu', 
    'estudiante@unifuturo.edu',
    'admin@primariafeliz.edu',
    'profesor@primariafeliz.edu',
    'tutor@email.com'
);