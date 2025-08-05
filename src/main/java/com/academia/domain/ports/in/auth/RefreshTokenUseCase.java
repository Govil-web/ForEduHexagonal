// RefreshTokenUseCase.java
package com.academia.domain.ports.in.auth;

import com.academia.domain.ports.in.commands.RefreshTokenCommand;
import com.academia.domain.ports.in.dtos.TokenRefreshResponseDTO;

/**
 * Puerto de entrada para el caso de uso de renovación de tokens.
 */
public interface RefreshTokenUseCase {
    /**
     * Renueva un access token usando un refresh token válido.
     * Implementa token rotation: se genera un nuevo refresh token y se invalida el anterior.
     *
     * @param command Comando con el refresh token
     * @return DTO con los nuevos tokens
     * @throws IllegalArgumentException si el refresh token es inválido o expirado
     */
    TokenRefreshResponseDTO refreshToken(RefreshTokenCommand command);
}