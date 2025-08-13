package com.academia.domain.ports.out;

import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.user.Email;

import java.util.Optional;

/**
 * Puerto de salida para el índice global de emails.
 * Define las operaciones necesarias para buscar organizaciones por email de forma global.
 */
public interface EmailOrganizationIndexRepository {
    
    /**
     * Busca el ID de la organización asociada a un email.
     * 
     * @param email El email a buscar
     * @return Optional con el ID de la organización si existe, o empty si no existe
     */
    Optional<OrganizationId> findOrganizationByEmail(Email email);
    
    /**
     * Verifica si un email existe en el índice global.
     * 
     * @param email El email a verificar
     * @return true si el email existe, false en caso contrario
     */
    boolean emailExistsGlobally(Email email);
    
    /**
     * Registra un nuevo email en el índice global.
     * 
     * @param email El email a registrar
     * @param organizationId El ID de la organización
     * @param userId El ID del usuario
     */
    void registerEmail(Email email, OrganizationId organizationId, Long userId);
}