-- =================================================================
-- MIGRACIÓN V6: ÍNDICE GLOBAL DE EMAILS PARA LOGIN SIMPLIFICADO
-- VERSIÓN CORREGIDA: Maneja usuarios del sistema (organization_id NULL)
-- =================================================================

-- -----------------------------------------------------
-- Tabla: email_organization_index
-- Índice global para encontrar la organización por email
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

    -- Índices optimizados para búsqueda
                                          INDEX idx_email_org_email (email),
                                          INDEX idx_email_org_user (user_id),
                                          INDEX idx_email_org_organization (organization_id),

    -- Índice compuesto para consultas rápidas
                                          INDEX idx_email_org_lookup (email, organization_id)
);

-- -----------------------------------------------------
-- Triggers automáticos para mantener sincronización
-- -----------------------------------------------------

-- Trigger para INSERT en users
DELIMITER $$
CREATE TRIGGER tr_users_insert_email_index
    AFTER INSERT ON users
    FOR EACH ROW
BEGIN
    -- Solo insertar si el usuario pertenece a una organización (no es super admin)
    IF NEW.organization_id IS NOT NULL THEN
        INSERT INTO email_organization_index (email, organization_id, user_id)
        VALUES (NEW.email, NEW.organization_id, NEW.id);
    END IF;
END$$
DELIMITER ;

-- Trigger para UPDATE en users (cambio de email)
DELIMITER $$
CREATE TRIGGER tr_users_update_email_index
    AFTER UPDATE ON users
    FOR EACH ROW
BEGIN
    -- Solo procesar si el usuario pertenece a una organización
    IF NEW.organization_id IS NOT NULL THEN
        -- Si cambió el email, actualizar el índice
        IF OLD.email != NEW.email THEN
            UPDATE email_organization_index
            SET email = NEW.email, updated_at = CURRENT_TIMESTAMP
            WHERE user_id = NEW.id;
        END IF;
    END IF;
END$$
DELIMITER ;

-- Trigger para DELETE en users
DELIMITER $$
CREATE TRIGGER tr_users_delete_email_index
    AFTER DELETE ON users
    FOR EACH ROW
BEGIN
    -- Eliminar del índice si existía
    DELETE FROM email_organization_index WHERE user_id = OLD.id;
END$$
DELIMITER ;

-- -----------------------------------------------------
-- Poblar tabla con datos existentes
-- CORRECCIÓN: Filtrar usuarios del sistema (organization_id NULL)
-- -----------------------------------------------------
INSERT INTO email_organization_index (email, organization_id, user_id)
SELECT email, organization_id, id
FROM users
WHERE organization_id IS NOT NULL  -- ← CORRIGE EL PROBLEMA DEL SUPER ADMIN
  AND email NOT IN (SELECT email FROM email_organization_index)
ORDER BY id;

-- -----------------------------------------------------
-- Verificación de integridad
-- -----------------------------------------------------

-- Verificar que no hay emails duplicados en el índice
SELECT email, COUNT(*) as count
FROM email_organization_index
GROUP BY email
HAVING COUNT(*) > 1;

-- Mostrar estadísticas de la migración
SELECT
    COUNT(*) as total_users,
    COUNT(CASE WHEN organization_id IS NOT NULL THEN 1 END) as org_users,
    COUNT(CASE WHEN organization_id IS NULL THEN 1 END) as system_users
FROM users;

SELECT COUNT(*) as indexed_emails FROM email_organization_index;