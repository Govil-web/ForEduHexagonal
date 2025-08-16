-- =================================================================
-- MIGRACIÓN V1: ESQUEMA BASE PARA SISTEMA ACADÉMICO ENTERPRISE
-- Arquitectura Hexagonal + DDD + Multi-Tenancy
-- Versión actualizada y coherente con el código actual
-- =================================================================

-- -----------------------------------------------------
-- Tabla: organizations (Tenants principales)
-- -----------------------------------------------------
CREATE TABLE organizations (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               uuid VARCHAR(36) NOT NULL UNIQUE,
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
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       organization_id BIGINT, -- NULL para super admins del sistema
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
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          email VARCHAR(100) NOT NULL UNIQUE,
                                          organization_id BIGINT NOT NULL,
                                          user_id BIGINT NOT NULL,
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
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                token VARCHAR(500) NOT NULL UNIQUE,
                                user_id BIGINT NOT NULL,
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
-- Tabla: user_roles (Asignación de roles)
-- -----------------------------------------------------
CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL,
                            role_id INT NOT NULL,
                            assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

                            PRIMARY KEY (user_id, role_id),
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Tabla: student_profiles (Perfil específico de estudiantes)
-- -----------------------------------------------------
CREATE TABLE student_profiles (
                                  user_id BIGINT PRIMARY KEY,
                                  organization_id BIGINT NOT NULL,
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
                                user_id BIGINT PRIMARY KEY,
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
                                   user_id BIGINT PRIMARY KEY,
                                   occupation VARCHAR(100),
                                   is_financial_responsible BOOLEAN DEFAULT FALSE,

                                   FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Tabla: student_guardian_relationships (Relación estudiante-tutor)
-- -----------------------------------------------------
CREATE TABLE student_guardian_relationships (
                                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                student_user_id BIGINT NOT NULL,
                                                guardian_user_id BIGINT NOT NULL,
                                                relationship_type VARCHAR(50) NOT NULL,
                                                is_primary_contact BOOLEAN DEFAULT FALSE,

                                                FOREIGN KEY (student_user_id) REFERENCES student_profiles(user_id) ON DELETE CASCADE,
                                                FOREIGN KEY (guardian_user_id) REFERENCES guardian_profiles(user_id) ON DELETE CASCADE,
                                                UNIQUE KEY uk_student_guardian (student_user_id, guardian_user_id)
);

-- -----------------------------------------------------
-- Tabla: subjects (Catálogo de materias)
-- -----------------------------------------------------
CREATE TABLE subjects (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          organization_id BIGINT NOT NULL,
                          name VARCHAR(100) NOT NULL,
                          subject_code VARCHAR(20) NOT NULL,
                          grade_level VARCHAR(50),
                          description TEXT,
                          is_active BOOLEAN DEFAULT TRUE,

                          FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
                          UNIQUE KEY uk_subject_code_organization (organization_id, subject_code)
);

-- -----------------------------------------------------
-- Tabla: academic_terms (Períodos académicos)
-- -----------------------------------------------------
CREATE TABLE academic_terms (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                organization_id BIGINT NOT NULL,
                                name VARCHAR(100) NOT NULL,
                                start_date DATE NOT NULL,
                                end_date DATE NOT NULL,

                                FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Tabla: courses (Cursos específicos)
-- -----------------------------------------------------
CREATE TABLE courses (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         subject_id BIGINT NOT NULL,
                         academic_term_id BIGINT NOT NULL,
                         teacher_user_id BIGINT,
                         course_code VARCHAR(50) NOT NULL,

                         FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
                         FOREIGN KEY (academic_term_id) REFERENCES academic_terms(id) ON DELETE CASCADE,
                         FOREIGN KEY (teacher_user_id) REFERENCES staff_profiles(user_id) ON DELETE SET NULL,
                         UNIQUE KEY uk_course_code_in_term (academic_term_id, course_code)
);

-- -----------------------------------------------------
-- Tabla: enrollments (Inscripciones a cursos)
-- -----------------------------------------------------
CREATE TABLE enrollments (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             student_user_id BIGINT NOT NULL,
                             course_id BIGINT NOT NULL,
                             enrollment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             status ENUM('ACTIVE', 'WITHDRAWN', 'COMPLETED') NOT NULL,
                             final_grade DECIMAL(5,2),

                             FOREIGN KEY (student_user_id) REFERENCES student_profiles(user_id) ON DELETE CASCADE,
                             FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
                             UNIQUE KEY uk_student_course (student_user_id, course_id)
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
        INSERT INTO email_organization_index (email, organization_id, user_id)
        VALUES (NEW.email, NEW.organization_id, NEW.id);
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