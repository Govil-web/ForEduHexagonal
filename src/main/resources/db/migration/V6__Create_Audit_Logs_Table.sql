-- ===================================================================
-- V6: Creación de tabla de audit logs para seguridad
-- ===================================================================

CREATE TABLE audit_logs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    event_type VARCHAR(50) NOT NULL,
    user_id BIGINT,
    organization_id BIGINT,
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
    
    PRIMARY KEY (id),
    
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

-- Comentarios para documentación
ALTER TABLE audit_logs COMMENT = 'Tabla de auditoría para eventos de seguridad y acceso';
ALTER TABLE audit_logs MODIFY COLUMN event_type VARCHAR(50) COMMENT 'Tipo de evento de auditoría (LOGIN_SUCCESS, LOGIN_FAILURE, etc.)';
ALTER TABLE audit_logs MODIFY COLUMN user_id BIGINT COMMENT 'ID del usuario (opcional para eventos no autenticados)';
ALTER TABLE audit_logs MODIFY COLUMN organization_id BIGINT COMMENT 'ID de la organización';
ALTER TABLE audit_logs MODIFY COLUMN user_email VARCHAR(255) COMMENT 'Email del usuario para trazabilidad';
ALTER TABLE audit_logs MODIFY COLUMN ip_address VARCHAR(45) COMMENT 'Dirección IP del cliente (IPv4/IPv6)';
ALTER TABLE audit_logs MODIFY COLUMN user_agent TEXT COMMENT 'User-Agent del navegador/cliente';
ALTER TABLE audit_logs MODIFY COLUMN resource VARCHAR(255) COMMENT 'Recurso accedido (URL, endpoint, etc.)';
ALTER TABLE audit_logs MODIFY COLUMN action VARCHAR(100) COMMENT 'Acción realizada (GET, POST, etc.)';
ALTER TABLE audit_logs MODIFY COLUMN success BOOLEAN COMMENT 'Indica si la operación fue exitosa';
ALTER TABLE audit_logs MODIFY COLUMN failure_reason VARCHAR(500) COMMENT 'Razón del fallo si success=false';
ALTER TABLE audit_logs MODIFY COLUMN metadata JSON COMMENT 'Metadatos adicionales en formato JSON';
ALTER TABLE audit_logs MODIFY COLUMN session_id VARCHAR(100) COMMENT 'ID de sesión para correlación';
ALTER TABLE audit_logs MODIFY COLUMN request_id VARCHAR(100) COMMENT 'ID de request para trazabilidad';
ALTER TABLE audit_logs MODIFY COLUMN timestamp DATETIME(6) COMMENT 'Timestamp con microsegundos del evento';