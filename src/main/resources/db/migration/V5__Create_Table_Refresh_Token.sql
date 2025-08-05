-- =================================================================
-- MIGRACIÓN V5: TABLA PARA REFRESH TOKENS JWT
-- Soporte para el sistema de autenticación con token rotation
-- =================================================================

-- -----------------------------------------------------
-- Tabla: refresh_tokens
-- Almacena los refresh tokens para implementar token rotation
-- y permitir invalidación de sesiones
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
                                INDEX idx_refresh_tokens_user_id (user_id),
                                INDEX idx_refresh_tokens_token (token),
                                INDEX idx_refresh_tokens_expires_at (expires_at),
                                INDEX idx_refresh_tokens_active (user_id, is_revoked, expires_at)
);

-- -----------------------------------------------------
-- Índices adicionales para optimizar consultas comunes
-- -----------------------------------------------------

-- Índice compuesto para buscar tokens válidos rápidamente
CREATE INDEX idx_refresh_tokens_valid
    ON refresh_tokens (token, is_revoked, expires_at);

-- Índice para limpieza de tokens expirados
CREATE INDEX idx_refresh_tokens_cleanup
    ON refresh_tokens (is_revoked, expires_at);