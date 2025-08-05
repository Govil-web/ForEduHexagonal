// LoginUseCase.java
package com.academia.domain.ports.in.auth;

import com.academia.domain.ports.in.commands.LoginCommand;
import com.academia.domain.ports.in.dtos.AuthenticationResponseDTO;

/**
 * Puerto de entrada para el caso de uso de autenticación de usuarios.
 */
public interface LoginUseCase {
    /**
     * Autentica un usuario usando email y contraseña.
     *
     * @param command Comando con las credenciales del usuario
     * @return DTO con tokens JWT y información del usuario
     * @throws IllegalArgumentException si las credenciales son inválidas
     * @throws IllegalStateException si la cuenta no puede autenticarse (ej. TUTOR_MANAGED)
     */
    AuthenticationResponseDTO login(LoginCommand command);
}