package com.academia.domain.ports.in.commands;

/**
 * Comando inmutable que representa la intención de crear una nueva organización.
 * Incluye los datos del administrador inicial que será creado automáticamente.
 */
public record CreateOrganizationCommand(
        String organizationName,
        String subdomain,
        int digitalConsentAge,
        // Datos del administrador inicial
        String adminFirstName,
        String adminLastName,
        String adminEmail,
        String adminPassword // En un caso real, vendría ya hasheada o se enviaría por canal seguro
) {
    /**
     * Validaciones básicas del comando en el constructor.
     */
    public CreateOrganizationCommand {
        if (organizationName == null || organizationName.isBlank()) {
            throw new IllegalArgumentException("El nombre de la organización no puede estar vacío");
        }
        if (subdomain == null || subdomain.isBlank()) {
            throw new IllegalArgumentException("El subdominio no puede estar vacío");
        }
        if (digitalConsentAge < 13 || digitalConsentAge > 21) {
            throw new IllegalArgumentException("La edad de consentimiento digital debe estar entre 13 y 21 años");
        }
        if (adminEmail == null || adminEmail.isBlank()) {
            throw new IllegalArgumentException("El email del administrador no puede estar vacío");
        }
        if (adminFirstName == null || adminFirstName.isBlank()) {
            throw new IllegalArgumentException("El nombre del administrador no puede estar vacío");
        }
        if (adminLastName == null || adminLastName.isBlank()) {
            throw new IllegalArgumentException("El apellido del administrador no puede estar vacío");
        }
    }
}