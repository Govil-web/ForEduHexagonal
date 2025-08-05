package com.academia.infrastructure.web.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para la petición HTTP de renovación de token.
 */
@Schema(description = "Datos requeridos para renovar un token de acceso")
public record RefreshTokenRequest(

        @NotBlank(message = "El refresh token es obligatorio")
        @Schema(description = "Refresh token válido", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String refreshToken
) {}
