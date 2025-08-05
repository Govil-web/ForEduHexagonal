package com.academia.infrastructure.web.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/**
 * DTO para la petición HTTP de creación de una nueva organización.
 * Incluye validaciones a nivel de API y documentación OpenAPI.
 */
@Schema(description = "Datos requeridos para crear una nueva organización")
public record CreateOrganizationRequest(

        @NotBlank(message = "El nombre de la organización es obligatorio")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        @Schema(description = "Nombre de la organización", example = "Universidad Tecnológica del Futuro")
        String organizationName,

        @NotBlank(message = "El subdominio es obligatorio")
        @Size(min = 3, max = 50, message = "El subdominio debe tener entre 3 y 50 caracteres")
        @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$",
                message = "El subdominio solo puede contener letras minúsculas, números y guiones")
        @Schema(description = "Subdominio único para la organización", example = "unitecfuturo")
        String subdomain,

        @Min(value = 13, message = "La edad de consentimiento digital mínima es 13 años")
        @Max(value = 21, message = "La edad de consentimiento digital máxima es 21 años")
        @Schema(description = "Edad mínima para el consentimiento digital", example = "16", minimum = "13", maximum = "21")
        int digitalConsentAge,

        // Datos del administrador inicial
        @NotBlank(message = "El nombre del administrador es obligatorio")
        @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
        @Schema(description = "Nombre del administrador inicial", example = "Ana")
        String adminFirstName,

        @NotBlank(message = "El apellido del administrador es obligatorio")
        @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
        @Schema(description = "Apellido del administrador inicial", example = "García")
        String adminLastName,

        @NotBlank(message = "El email del administrador es obligatorio")
        @Email(message = "El email debe tener un formato válido")
        @Schema(description = "Email del administrador inicial", example = "admin@unitecfuturo.edu")
        String adminEmail,

        @NotBlank(message = "La contraseña del administrador es obligatoria")
        @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
                message = "La contraseña debe contener al menos una minúscula, una mayúscula y un número")
        @Schema(description = "Contraseña del administrador inicial (mín. 8 caracteres, con mayúscula, minúscula y número)",
                example = "AdminPass123")
        String adminPassword
) {}
