package com.academia.infrastructure.web.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para la petición HTTP de login.
 */
@Schema(description = "Datos requeridos para autenticar un usuario")
public record LoginRequest(

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato válido")
        @Schema(description = "Email del usuario", example = "admin@unitecfuturo.edu")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
        @Schema(description = "Contraseña del usuario", example = "AdminPass123")
        String password,

        @NotBlank(message = "El subdominio de la organización es obligatorio")
        @Size(min = 3, max = 50, message = "El subdominio debe tener entre 3 y 50 caracteres")
        @Schema(description = "Subdominio de la organización", example = "unitecfuturo")
        String organizationSubdomain
) {}
