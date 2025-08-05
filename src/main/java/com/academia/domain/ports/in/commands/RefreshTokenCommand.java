// RefreshTokenCommand.java
package com.academia.domain.ports.in.commands;

/**
 * Comando para renovar tokens JWT usando un refresh token.
 */
public record RefreshTokenCommand(
        String refreshToken
) {
    public RefreshTokenCommand {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("El refresh token no puede estar vacío");
        }
    }
}