-- =================================================================
-- MIGRACIÓN V2: DATOS DE PRUEBA COMPLETOS PARA LA PLATAFORMA SAAS
-- =================================================================

-- -----------------------------------------------------
-- Roles del Sistema y de Tenant
-- -----------------------------------------------------
INSERT INTO roles (id, name, scope) VALUES
                                        (1, 'SYSTEM_ADMIN', 'SYSTEM'),
                                        (2, 'ORGANIZATION_ADMIN', 'TENANT'),
                                        (3, 'ACADEMIC_DIRECTOR', 'TENANT'),
                                        (4, 'TEACHER', 'TENANT'),
                                        (5, 'STUDENT', 'TENANT'),
                                        (6, 'GUARDIAN', 'TENANT');

-- -----------------------------------------------------
-- Super User (Administrador de la Plataforma)
-- Pass: superadmin123
-- -----------------------------------------------------
INSERT INTO users (id, organization_id, first_name, last_name, email, password_hash, birth_date, account_status) VALUES
    (1, NULL, 'Plataforma', 'Admin', 'superadmin@plataforma.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '1990-01-01', 'ACTIVE');
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);

-- =====================================================
-- TENANT 1: Primaria "Mi Pequeño Mundo" (Edad de consentimiento: 18)
-- =====================================================
INSERT INTO organizations (id, uuid, name, subdomain, digital_consent_age) VALUES (1, UUID(), 'Primaria Mi Pequeño Mundo', 'pequenomundo', 18);

-- Admin de la Organización
-- Pass: adminmundo123
INSERT INTO users (organization_id, first_name, last_name, email, password_hash, birth_date, account_status) VALUES
    (1, 'Ana', 'Directora', 'directora.ana@pequenomundo.edu', '$2a$10$Y.aV.qCgfj4j.mCj/wJGm.U/4J4.gY5.e/3.4h5gQh/e6gQ2iV1Z.', '1980-05-10', 'ACTIVE');
SET @admin_mundo_id = LAST_INSERT_ID();
INSERT INTO user_roles (user_id, role_id) VALUES (@admin_mundo_id, 2);
INSERT INTO staff_profiles (user_id, title) VALUES (@admin_mundo_id, 'Directora General');

-- Student MENOR de edad (Estado: TUTOR_MANAGED, porque 8 años < 18)
INSERT INTO users (organization_id, first_name, last_name, email, password_hash, birth_date, account_status) VALUES
    (1, 'Carlos', 'Ruiz', 'carlos.ruiz@pequenomundo.edu', NULL, '2016-08-20', 'TUTOR_MANAGED');
SET @carlos_id = LAST_INSERT_ID();
INSERT INTO user_roles (user_id, role_id) VALUES (@carlos_id, 5);
INSERT INTO student_profiles (user_id, organization_id, student_id_number, enrollment_date, current_grade_level) VALUES (@carlos_id, 1, 'PM-2024-001', '2024-02-01', '3er Grado');

-- Tutor del menor de edad
-- Pass: marianapass123
INSERT INTO users (organization_id, first_name, last_name, email, password_hash, birth_date, account_status) VALUES
    (1, 'Mariana', 'Ruiz', 'mariana.ruiz@email.com', '$2a$10$Y.aV.qCgfj4j.mCj/wJGm.U/4J4.gY5.e/3.4h5gQh/e6gQ2iV1Z.', '1988-11-05', 'ACTIVE');
SET @mariana_id = LAST_INSERT_ID();
INSERT INTO user_roles (user_id, role_id) VALUES (@mariana_id, 6);
INSERT INTO guardian_profiles (user_id, occupation, is_financial_responsible) VALUES (@mariana_id, 'Abogada', TRUE);

-- Vincular student y tutor
INSERT INTO student_guardian_relationships (student_user_id, guardian_user_id, relationship_type, is_primary_contact) VALUES (@carlos_id, @mariana_id, 'Madre', TRUE);

-- =====================================================
-- TENANT 2: "Universidad del Futuro" (Edad de consentimiento: 16)
-- =====================================================
INSERT INTO organizations (id, uuid, name, subdomain, digital_consent_age) VALUES (2, UUID(), 'Universidad del Futuro', 'unifuturo', 16);

-- Admin de la Organización
-- Pass: adminfuturo123
INSERT INTO users (organization_id, first_name, last_name, email, password_hash, birth_date, account_status) VALUES
    (2, 'Rector', 'Morales', 'rector.morales@unifuturo.edu', '$2a$10$Y.aV.qCgfj4j.mCj/wJGm.U/4J4.gY5.e/3.4h5gQh/e6gQ2iV1Z.', '1970-01-25', 'ACTIVE');
SET @admin_futuro_id = LAST_INSERT_ID();
INSERT INTO user_roles (user_id, role_id) VALUES (@admin_futuro_id, 2);
INSERT INTO staff_profiles (user_id, title) VALUES (@admin_futuro_id, 'Rector');

-- Student MENOR de edad pero MAYOR que la política del tenant (17 años > 16) (Estado: ACTIVE)
-- Pass: luisvega123
INSERT INTO users (organization_id, first_name, last_name, email, password_hash, birth_date, account_status) VALUES
    (2, 'Luis', 'Vega', 'luis.vega@unifuturo.edu', '$2a$10$Y.aV.qCgfj4j.mCj/wJGm.U/4J4.gY5.e/3.4h5gQh/e6gQ2iV1Z.', '2008-05-10', 'ACTIVE');
SET @luis_id = LAST_INSERT_ID();
INSERT INTO user_roles (user_id, role_id) VALUES (@luis_id, 5);
INSERT INTO student_profiles (user_id, organization_id, student_id_number, enrollment_date, current_grade_level) VALUES (@luis_id, 2, 'UF-2025-001', '2025-02-01', 'Arquitectura - Semestre 1');