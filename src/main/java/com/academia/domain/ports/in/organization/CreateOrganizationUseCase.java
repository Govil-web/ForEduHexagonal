package com.academia.domain.ports.in.organization;

import com.academia.domain.ports.in.commands.CreateOrganizationCommand;
import com.academia.domain.ports.in.dtos.OrganizationDetailsDTO;

/**
 * Puerto de entrada para el caso de uso de creación de organizaciones.
 * Define el contrato que debe cumplir la implementación en la capa de aplicación.
 */
public interface CreateOrganizationUseCase {
    /**
     * Crea una nueva organización en el sistema junto con su administrador inicial.
     *
     * @param command El comando con los datos necesarios para crear la organización
     * @return DTO con los detalles de la organización creada
     * @throws com.academia.application.exceptions.ResourceNotFoundException si algún recurso requerido no existe
     * @throws IllegalArgumentException si los datos del comando no son válidos
     */
    OrganizationDetailsDTO createOrganization(CreateOrganizationCommand command);
}
