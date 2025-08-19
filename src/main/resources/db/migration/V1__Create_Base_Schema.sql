-- =================================================================
-- MIGRACIÓN V1: ESQUEMA BASE DEL SISTEMA ACADÉMico ENTERPRISE
-- Arquitectura Hexagonal + DDD + Multi-Tenancy
-- Crea todas las tablas principales sin dependencias circulares
-- =================================================================

-- -----------------------------------------------------
-- Tabla: organizations (Tenants principales)
-- -----------------------------------------------------
CREATE TABLE organizations (
                               id CHAR(36) PRIMARY KEY,
                               name VARCHAR(100) NOT NULL,
                               subdomain VARCHAR(50) NOT NULL UNIQUE,
                               is_active BOOLEAN DEFAULT TRUE NOT NULL,
                               digital_consent_age INT NOT NULL DEFAULT 18,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,

                               INDEX idx_organizations_subdomain (subdomain),
                               INDEX idx_organizations_active (is_active)
);

-- -----------------------------------------------------
-- Tabla: users (Identidad central unificada)
-- -----------------------------------------------------
CREATE TABLE users (
                       id CHAR(36) PRIMARY KEY,
                       organization_id CHAR(36), -- NULL para super admins del sistema
                       first_name VARCHAR(50) NOT NULL,
                       last_name VARCHAR(50) NOT NULL,
                       dni VARCHAR(20),
                       email VARCHAR(100) NOT NULL,
                       password_hash VARCHAR(255), -- NULL para cuentas TUTOR_MANAGED
                       birth_date DATE NOT NULL,
                       phone_number VARCHAR(20),
                       account_status ENUM('PENDING_VERIFICATION', 'ACTIVE', 'TUTOR_MANAGED', 'SUSPENDED', 'DEACTIVATED') NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,

                       FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
                       UNIQUE KEY uk_user_email_organization (email, organization_id),
                       INDEX idx_users_organization (organization_id),
                       INDEX idx_users_email (email),
                       INDEX idx_users_status (account_status)
);

-- -----------------------------------------------------
-- Tabla: email_organization_index (Índice global para login)
-- -----------------------------------------------------
CREATE TABLE email_organization_index (
                                          id CHAR(36) PRIMARY KEY,
                                          email VARCHAR(100) NOT NULL UNIQUE,
                                          organization_id CHAR(36) NOT NULL,
                                          user_id CHAR(36) NOT NULL,
                                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,

                                          FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
                                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

                                          INDEX idx_email_lookup (email),
                                          INDEX idx_user_lookup (user_id)
);

-- -----------------------------------------------------
-- Tabla: refresh_tokens (Para autenticación JWT)
-- -----------------------------------------------------
CREATE TABLE refresh_tokens (
                                id CHAR(36) PRIMARY KEY,
                                token VARCHAR(500) NOT NULL UNIQUE,
                                user_id CHAR(36) NOT NULL,
                                expires_at TIMESTAMP NOT NULL,
                                is_revoked BOOLEAN DEFAULT FALSE NOT NULL,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                revoked_at TIMESTAMP NULL,

                                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                INDEX idx_refresh_tokens_user (user_id),
                                INDEX idx_refresh_tokens_token (token),
                                INDEX idx_refresh_tokens_valid (user_id, is_revoked, expires_at)
);

-- -----------------------------------------------------
-- Tabla: roles (Sistema RBAC)
-- -----------------------------------------------------
CREATE TABLE roles (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(50) NOT NULL UNIQUE,
                       scope ENUM('SYSTEM', 'TENANT') NOT NULL
);

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
-- Tabla: user_roles (Asignación de roles)
-- -----------------------------------------------------
CREATE TABLE user_roles (
                            user_id CHAR(36) NOT NULL,
                            role_id INT NOT NULL,
                            assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

                            PRIMARY KEY (user_id, role_id),
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
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
-- Tabla: student_profiles (Perfil específico de estudiantes)
-- -----------------------------------------------------
CREATE TABLE student_profiles (
                                  user_id CHAR(36) PRIMARY KEY,
                                  organization_id CHAR(36) NOT NULL,
                                  student_id_number VARCHAR(20) NOT NULL, -- Legajo
                                  enrollment_date DATE NOT NULL,
                                  current_grade_level VARCHAR(50),

                                  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                  FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
                                  UNIQUE KEY uk_student_id_organization (organization_id, student_id_number)
);

-- -----------------------------------------------------
-- Tabla: staff_profiles (Perfil específico de staff)
-- -----------------------------------------------------
CREATE TABLE staff_profiles (
                                user_id CHAR(36) PRIMARY KEY,
                                employee_id_number VARCHAR(50),
                                hire_date DATE,
                                department VARCHAR(100),
                                title VARCHAR(100),

                                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Tabla: guardian_profiles (Perfil específico de tutores)
-- -----------------------------------------------------
CREATE TABLE guardian_profiles (
                                   user_id CHAR(36) PRIMARY KEY,
                                   occupation VARCHAR(100),
                                   is_financial_responsible BOOLEAN DEFAULT FALSE,

                                   FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Tabla: student_guardian_relationships (Relación estudiante-tutor)
-- -----------------------------------------------------
CREATE TABLE student_guardian_relationships (
                                                id CHAR(36) PRIMARY KEY,
                                                student_user_id CHAR(36) NOT NULL,
                                                guardian_user_id CHAR(36) NOT NULL,
                                                relationship_type VARCHAR(50) NOT NULL,
                                                is_primary_contact BOOLEAN DEFAULT FALSE,

                                                FOREIGN KEY (student_user_id) REFERENCES student_profiles(user_id) ON DELETE CASCADE,
                                                FOREIGN KEY (guardian_user_id) REFERENCES guardian_profiles(user_id) ON DELETE CASCADE,
                                                UNIQUE KEY uk_student_guardian (student_user_id, guardian_user_id)
);

-- -----------------------------------------------------
-- Tabla: subjects (Materias/Asignaturas)
-- -----------------------------------------------------
CREATE TABLE subjects (
                          id CHAR(36) PRIMARY KEY,
                          organization_id CHAR(36) NOT NULL,
                          name VARCHAR(255) NOT NULL,
                          subject_code VARCHAR(20) NOT NULL,
                          description TEXT,
                          credits INT NOT NULL DEFAULT 1,
                          grade_level VARCHAR(50),
                          is_active BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,

    -- Constraints
                          FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
                          UNIQUE KEY uk_subjects_org_code (organization_id, subject_code),
                          CONSTRAINT chk_subjects_credits_positive CHECK (credits > 0),
                          CONSTRAINT chk_subjects_name_not_empty CHECK (LENGTH(TRIM(name)) > 0),
                          CONSTRAINT chk_subjects_code_not_empty CHECK (LENGTH(TRIM(subject_code)) > 0),

    -- Índices
                          INDEX idx_subjects_organization_id (organization_id),
                          INDEX idx_subjects_organization_active (organization_id, is_active),
                          INDEX idx_subjects_code (subject_code),
                          INDEX idx_subjects_name (name)
);

-- -----------------------------------------------------
-- Tabla: academic_terms (Períodos Académicos)
-- -----------------------------------------------------
CREATE TABLE academic_terms (
                                id CHAR(36) PRIMARY KEY,
                                organization_id CHAR(36) NOT NULL,
                                name VARCHAR(255) NOT NULL,
                                start_date DATE NOT NULL,
                                end_date DATE NOT NULL,
                                is_active BOOLEAN NOT NULL DEFAULT TRUE,
                                is_current_term BOOLEAN NOT NULL DEFAULT FALSE,
                                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,

    -- Constraints
                                FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
                                UNIQUE KEY uk_academic_terms_org_name (organization_id, name),
                                CONSTRAINT chk_academic_terms_dates CHECK (start_date < end_date),
                                CONSTRAINT chk_academic_terms_name_not_empty CHECK (LENGTH(TRIM(name)) > 0),

    -- Índices
                                INDEX idx_academic_terms_organization_id (organization_id),
                                INDEX idx_academic_terms_organization_active (organization_id, is_active),
                                INDEX idx_academic_terms_organization_current (organization_id, is_current_term),
                                INDEX idx_academic_terms_dates (start_date, end_date),
                                INDEX idx_academic_terms_current_active (is_current_term, is_active)
);

-- -----------------------------------------------------
-- Tabla: courses (Cursos específicos)
-- -----------------------------------------------------
CREATE TABLE courses (
                         id CHAR(36) PRIMARY KEY,
                         subject_id CHAR(36) NOT NULL,
                         academic_term_id CHAR(36) NOT NULL,
                         teacher_user_id CHAR(36),
                         course_code VARCHAR(50) NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                         FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
                         FOREIGN KEY (academic_term_id) REFERENCES academic_terms(id) ON DELETE CASCADE,
                         FOREIGN KEY (teacher_user_id) REFERENCES staff_profiles(user_id) ON DELETE SET NULL,
                         UNIQUE KEY uk_course_code_in_term (academic_term_id, course_code)
);

-- -----------------------------------------------------
-- Tabla: enrollments (Inscripciones a cursos)
-- -----------------------------------------------------
CREATE TABLE enrollments (
                             id CHAR(36) PRIMARY KEY,
                             student_user_id CHAR(36) NOT NULL,
                             course_id CHAR(36) NOT NULL,
                             enrollment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             status ENUM('ACTIVE', 'WITHDRAWN', 'COMPLETED') NOT NULL,
                             final_grade DECIMAL(5,2),

                             FOREIGN KEY (student_user_id) REFERENCES student_profiles(user_id) ON DELETE CASCADE,
                             FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
                             UNIQUE KEY uk_student_course (student_user_id, course_id)
);

-- -----------------------------------------------------
-- Tabla: audit_logs (Auditoría de seguridad)
-- -----------------------------------------------------
CREATE TABLE audit_logs (
                            id CHAR(36) PRIMARY KEY,
                            event_type VARCHAR(50) NOT NULL,
                            user_id CHAR(36),
                            organization_id CHAR(36),
                            user_email VARCHAR(255),
                            ip_address VARCHAR(45),
                            user_agent TEXT,
                            resource VARCHAR(255),
                            action VARCHAR(100),
                            success BOOLEAN NOT NULL DEFAULT FALSE,
                            failure_reason VARCHAR(500),
                            metadata JSON,
                            session_id VARCHAR(100),
                            request_id VARCHAR(100),
                            timestamp DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    -- Índices para optimizar consultas de seguridad
                            INDEX idx_audit_event_type (event_type),
                            INDEX idx_audit_user_id (user_id),
                            INDEX idx_audit_organization_id (organization_id),
                            INDEX idx_audit_user_email (user_email),
                            INDEX idx_audit_ip_address (ip_address),
                            INDEX idx_audit_timestamp (timestamp),
                            INDEX idx_audit_success (success),

    -- Índices compuestos para consultas frecuentes
                            INDEX idx_audit_user_timestamp (user_id, timestamp),
                            INDEX idx_audit_org_timestamp (organization_id, timestamp),
                            INDEX idx_audit_event_timestamp (event_type, timestamp),
                            INDEX idx_audit_failed_logins (user_email, success, timestamp),
                            INDEX idx_audit_ip_timestamp (ip_address, timestamp),

    -- Claves foráneas (opcionales, pueden ser nulas para eventos no autenticados)
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
                            FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE SET NULL
);

-- -----------------------------------------------------
-- Tabla: organization_security_config (Configuración de seguridad)
-- -----------------------------------------------------
CREATE TABLE organization_security_config (
                                              id CHAR(36) PRIMARY KEY,
                                              organization_id CHAR(36) NOT NULL,
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

-- -----------------------------------------------------
-- Tabla: password_audit_log (Auditoría de contraseñas)
-- -----------------------------------------------------
CREATE TABLE password_audit_log (
                                    id CHAR(36) PRIMARY KEY,
                                    user_id CHAR(36) NOT NULL,
                                    action VARCHAR(50) NOT NULL, -- 'CHANGED', 'RESET', 'EXPIRED'
                                    performed_by CHAR(36), -- ID del usuario que realizó la acción (NULL si fue automático)
                                    ip_address VARCHAR(45),
                                    user_agent TEXT,
                                    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                    FOREIGN KEY (performed_by) REFERENCES users(id) ON DELETE SET NULL,
                                    INDEX idx_password_audit_user (user_id),
                                    INDEX idx_password_audit_timestamp (timestamp)
);

-- -----------------------------------------------------
-- Triggers para mantener sincronizado el índice de emails
-- -----------------------------------------------------
DELIMITER $$

-- Trigger para INSERT en users
CREATE TRIGGER tr_users_insert_email_index
    AFTER INSERT ON users
    FOR EACH ROW
BEGIN
    IF NEW.organization_id IS NOT NULL THEN
        INSERT INTO email_organization_index (id, email, organization_id, user_id)
        VALUES (UUID(), NEW.email, NEW.organization_id, NEW.id);
    END IF;
END$$

-- Trigger para UPDATE en users
CREATE TRIGGER tr_users_update_email_index
    AFTER UPDATE ON users
    FOR EACH ROW
BEGIN
    IF NEW.organization_id IS NOT NULL THEN
        IF OLD.email != NEW.email THEN
            UPDATE email_organization_index
            SET email = NEW.email, updated_at = CURRENT_TIMESTAMP
            WHERE user_id = NEW.id;
        END IF;
    END IF;
END$$

-- Trigger para DELETE en users
CREATE TRIGGER tr_users_delete_email_index
    AFTER DELETE ON users
    FOR EACH ROW
BEGIN
    DELETE FROM email_organization_index WHERE user_id = OLD.id;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- Comentarios para documentación
-- -----------------------------------------------------
ALTER TABLE organizations COMMENT = 'Tabla de organizaciones/tenants del sistema';
ALTER TABLE users COMMENT = 'Tabla central de usuarios del sistema';
ALTER TABLE subjects COMMENT = 'Tabla de materias/asignaturas del sistema académico';
ALTER TABLE academic_terms COMMENT = 'Tabla de períodos académicos (semestres, trimestres, etc.)';
ALTER TABLE courses COMMENT = 'Tabla de cursos específicos por materia y período';
ALTER TABLE enrollments COMMENT = 'Tabla de inscripciones de estudiantes a cursos';
ALTER TABLE audit_logs COMMENT = 'Tabla de auditoría para eventos de seguridad y acceso';