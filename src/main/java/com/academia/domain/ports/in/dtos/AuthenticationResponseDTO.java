// AuthenticationResponseDTO.java
package com.academia.domain.ports.in.dtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO que representa la respuesta de un login exitoso.
 * Contiene los tokens y información del usuario autenticado.
 */
public record AuthenticationResponseDTO(
        String accessToken,
        String refreshToken,
        LocalDateTime accessTokenExpiresAt,
        LocalDateTime refreshTokenExpiresAt,
        UserAuthInfoDTO user
) {
    /**
     * DTO anidado con información del usuario autenticado.
     */
    public record UserAuthInfoDTO(
            UUID accountId,
            String fullName,
            String email,
            String accountStatus,
            OrganizationInfoDTO organization,
            List<String> roles,
            List<String> permissions
    ) {}

    /**
     * DTO anidado con información de la organización.
     */
    public record OrganizationInfoDTO(
            UUID organizationId,
            String name,
            String subdomain
    ) {}
}