package com.academia.domain.ports.out;
import com.academia.domain.model.aggregates.Organization;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import java.util.Optional;

public interface OrganizationRepository {
    Organization save(Organization organization);
    Optional<Organization> findById(OrganizationId id);
    /**
     * Busca una organización por su subdominio único.
     * Necesario para el proceso de autenticación multi-tenant.
     *
     * @param subdomain Subdominio de la organización
     * @return Optional con la organización si existe
     */
    Optional<Organization> findBySubdomain(String subdomain);

    /**
     * Verifica si un subdominio ya está en uso.
     *
     * @param subdomain Subdominio a verificar
     * @return true si el subdominio existe
     */
    boolean existsBySubdomain(String subdomain);
}
