package com.academia.domain.ports.in.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO que representa los detalles de una organización para los clientes de la aplicación.
 * Incluye información del administrador inicial creado.
 */
public record OrganizationDetailsDTO(
        UUID organizationId,
        String name,
        String subdomain,
        int digitalConsentAge,
        boolean isActive,
        LocalDateTime createdAt,

        // Información del administrador inicial creado
        AdminDetailsDTO initialAdmin
) {
    /**
     * DTO anidado para los detalles del administrador inicial.
     */
    public record AdminDetailsDTO(
            UUID adminAccountId,
            String fullName,
            String email,
            String accountStatus
    ) {}
}
