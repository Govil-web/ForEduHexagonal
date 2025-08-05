// LogoutUseCase.java
package com.academia.domain.ports.in.auth;

/**
 * Puerto de entrada para el caso de uso de logout.
 */
public interface LogoutUseCase {
    /**
     * Invalida los tokens del usuario (blacklist).
     *
     * @param refreshToken Token a invalidar
     */
    void logout(String refreshToken);
}