-- =================================================================
-- MIGRACIÓN V1: ESQUEMA COMPLETO PARA PLATAFORMA EDUCATIVA SAAS
-- Diseñado para Multi-Tenancy, Flexibilidad y Escalabilidad.
-- VERSIÓN CORREGIDA Y ROBUSTA
-- =================================================================

-- -----------------------------------------------------
-- Tabla: organizations (Tenants)
-- Cada fila representa una institución cliente.
-- -----------------------------------------------------
CREATE TABLE organizations (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               uuid VARCHAR(36) NOT NULL UNIQUE,
                               name VARCHAR(100) NOT NULL,
                               subdomain VARCHAR(50) NOT NULL UNIQUE,
                               is_active BOOLEAN DEFAULT TRUE NOT NULL,
    -- Política clave para la gestión de menores de edad, configurable por tenant
                               digital_consent_age INT NOT NULL DEFAULT 18,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL
);

-- -----------------------------------------------------
-- Tabla: users (Identidad Central Unificada)
-- -----------------------------------------------------
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       organization_id BIGINT, -- NULO únicamente para los Administradores del Sistema (Super Users)
                       first_name VARCHAR(50) NOT NULL,
                       last_name VARCHAR(50) NOT NULL,
                       dni VARCHAR(20),
                       email VARCHAR(100) NOT NULL,
                       password_hash VARCHAR(255), -- NULO para cuentas que no pueden iniciar sesión (ej. menores)
                       birth_date DATE NOT NULL,
                       phone_number VARCHAR(20),
                       account_status ENUM('PENDING_VERIFICATION', 'ACTIVE', 'TUTOR_MANAGED', 'SUSPENDED', 'DEACTIVATED') NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
                       FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
                       UNIQUE KEY uk_user_email_organization (email, organization_id),
                       INDEX idx_users_account_status (account_status)
);

-- -----------------------------------------------------
-- Tablas para RBAC (Control de Acceso Basado en Roles)
-- -----------------------------------------------------
CREATE TABLE roles (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(50) NOT NULL UNIQUE,
                       scope ENUM('SYSTEM', 'TENANT') NOT NULL -- Define si el rol es a nivel de plataforma o de organización
);

CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL,
                            role_id INT NOT NULL,
                            PRIMARY KEY (user_id, role_id),
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Tablas de Perfiles Específicos (Extienden a 'users')
-- -----------------------------------------------------
CREATE TABLE staff_profiles (
                                user_id BIGINT PRIMARY KEY,
                                employee_id_number VARCHAR(50),
                                hire_date DATE,
                                department VARCHAR(100),
                                title VARCHAR(100), -- Ej: "Staff de Física", "Director Académico"
                                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE student_profiles (
                                  user_id BIGINT PRIMARY KEY,
                                  organization_id BIGINT NOT NULL, -- Redundancia controlada para la restricción UNIQUE
                                  student_id_number VARCHAR(20) NOT NULL, -- Legajo
                                  enrollment_date DATE NOT NULL,
                                  current_grade_level VARCHAR(50),
                                  observations TEXT,
                                  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                  FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE,
    -- Restricción UNIQUE corregida y funcional
                                  UNIQUE KEY uk_student_id_organization (organization_id, student_id_number)
);

CREATE TABLE guardian_profiles (
                                   user_id BIGINT PRIMARY KEY,
                                   occupation VARCHAR(100),
                                   is_financial_responsible BOOLEAN DEFAULT FALSE,
                                   FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Relación Student-Tutor (Guardián)
CREATE TABLE student_guardian_relationships (
                                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                student_user_id BIGINT NOT NULL,
                                                guardian_user_id BIGINT NOT NULL,
                                                relationship_type VARCHAR(50) NOT NULL, -- Padre, Madre, Tutor Legal, etc.
                                                is_primary_contact BOOLEAN DEFAULT FALSE,
                                                FOREIGN KEY (student_user_id) REFERENCES student_profiles(user_id) ON DELETE CASCADE,
                                                FOREIGN KEY (guardian_user_id) REFERENCES guardian_profiles(user_id) ON DELETE CASCADE,
                                                UNIQUE KEY uk_student_guardian (student_user_id, guardian_user_id)
);

-- -----------------------------------------------------
-- Catálogo de Materias (Currículo)
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
-- Períodos Académicos
-- -----------------------------------------------------
CREATE TABLE academic_terms (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                organization_id BIGINT NOT NULL,
                                name VARCHAR(100) NOT NULL, -- Ej: "Semestre 2025-1", "Año Lectivo 2026"
                                start_date DATE NOT NULL,
                                end_date DATE NOT NULL,
                                FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Cursos (Instancia de una subject en un período)
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
-- Inscripciones de Estudiantes a Cursos
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
-- Asistencias
-- -----------------------------------------------------
CREATE TABLE attendance_records (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    enrollment_id BIGINT NOT NULL,
                                    session_date DATE NOT NULL,
                                    status ENUM('PRESENT', 'ABSENT', 'LATE', 'EXCUSED') NOT NULL,
                                    notes TEXT,
                                    FOREIGN KEY (enrollment_id) REFERENCES enrollments(id) ON DELETE CASCADE,
                                    UNIQUE KEY uk_attendance_session (enrollment_id, session_date)
);

-- -----------------------------------------------------
-- Auditoría
-- -----------------------------------------------------
CREATE TABLE audit_log (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           organization_id BIGINT,
                           user_id BIGINT,
                           action VARCHAR(100) NOT NULL,
                           entity_type VARCHAR(50),
                           entity_id BIGINT,
                           details JSON,
                           timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                           FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE SET NULL,
                           FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);