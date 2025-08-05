package com.academia.domain.ports.out;

import com.academia.domain.model.aggregates.UserAccount;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Puerto de salida para la generación y validación de tokens JWT.
 */
public interface JwtTokenProvider {
    /**
     * Genera un access token JWT para el usuario autenticado.
     *
     * @param userAccount Agregado del usuario
     * @param organizationSubdomain Subdominio de la organización
     * @return Token JWT como string
     */
    String generateAccessToken(UserAccount userAccount, String organizationSubdomain);

    /**
     * Genera un refresh token para el usuario.
     *
     * @param userAccount Agregado del usuario
     * @return Refresh token como string
     */
    String generateRefreshToken(UserAccount userAccount);

    /**
     * Valida si un token JWT es válido y no ha expirado.
     *
     * @param token Token a validar
     * @return true si el token es válido
     */
    boolean validateToken(String token);

    /**
     * Extrae claims del token JWT.
     *
     * @param token Token JWT
     * @return Mapa con los claims
     */
    Map<String, Object> extractClaims(String token);

    /**
     * Extrae el email del usuario del token.
     *
     * @param token Token JWT
     * @return Email del usuario
     */
    String extractEmail(String token);

    /**
     * Obtiene la fecha de expiración del access token.
     *
     * @return Fecha y hora de expiración
     */
    LocalDateTime getAccessTokenExpiration();

    /**
     * Obtiene la fecha de expiración del refresh token.
     *
     * @return Fecha y hora de expiración
     */
    LocalDateTime getRefreshTokenExpiration();
}