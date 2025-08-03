package com.academia.domain.model.enums;

/**
 * Define el ámbito de un rol en el sistema SaaS.
 */
public enum RoleScope {
    /**
     * Un rol que opera a nivel de toda la plataforma, fuera de cualquier organización.
     * Ejemplo: SYSTEM_ADMIN.
     */
    SYSTEM,

    /**
     * Un rol que opera dentro de los límites de una organización específica.
     * Ejemplo: TEACHER, STUDENT.
     */
    TENANT
}