// LoginCommand.java
package com.academia.domain.ports.in.commands;

/**
 * Comando inmutable que representa la intención de autenticar un usuario.
 * Versión optimizada que no requiere el subdominio de la organización.
 */
public record LoginCommand(
        String email,
        String password
) {
    public LoginCommand {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
    }
}