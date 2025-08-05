package com.academia.application.services;

import com.academia.domain.model.aggregates.Organization;
import com.academia.domain.model.entities.User;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.ports.out.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Servicio para validar el contexto organizacional en operaciones multi-tenant.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationContextService {

    private final OrganizationRepository organizationRepository;

    /**
     * Valida que un usuario pertenece a la organización especificada.
     *
     * @param user Usuario a validar
     * @param organizationId ID de la organización
     * @return true si el usuario pertenece a la organización
     */
    public boolean userBelongsToOrganization(User user, OrganizationId organizationId) {
        if (user.getOrganizationId() == null && organizationId == null) {
            // Usuarios del sistema (sin organización)
            return true;
        }

        boolean belongs = user.getOrganizationId() != null &&
                user.getOrganizationId().equals(organizationId);

        if (!belongs) {
            log.warn("Usuario {} no pertenece a la organización {}",
                    user.getEmail().value(), organizationId != null ? organizationId.getValue() : "null");
        }

        return belongs;
    }

    /**
     * Valida que una organización está activa y puede ser usada.
     *
     * @param organization Organización a validar
     * @throws IllegalStateException si la organización no está activa
     */
    public void validateOrganizationIsActive(Organization organization) {
        if (!organization.isActive()) {
            log.warn("Intento de acceso a organización inactiva: {}", organization.getName());
            throw new IllegalStateException("La organización no está activa");
        }
    }

    /**
     * Obtiene la organización y valida que está activa.
     *
     * @param organizationId ID de la organización
     * @return Organización activa
     * @throws IllegalArgumentException si no existe
     * @throws IllegalStateException si no está activa
     */
    public Organization getActiveOrganization(OrganizationId organizationId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organización no encontrada"));

        validateOrganizationIsActive(organization);
        return organization;
    }
}