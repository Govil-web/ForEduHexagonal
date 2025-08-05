package com.academia.domain.ports.out;

import com.academia.domain.model.valueobjects.ids.AccountId;
import java.util.Optional;

/**
 * Puerto de salida para el repositorio de refresh tokens.
 */
public interface RefreshTokenRepository {
    /**
     * Guarda un refresh token en la base de datos.
     *
     * @param token Token a guardar
     * @param accountId ID de la cuenta asociada
     * @param expirationTime Tiempo de expiración en milisegundos
     */
    void saveRefreshToken(String token, AccountId accountId, long expirationTime);

    /**
     * Busca un refresh token en la base de datos.
     *
     * @param token Token a buscar
     * @return Optional con el ID de cuenta si existe
     */
    Optional<AccountId> findAccountByRefreshToken(String token);

    /**
     * Verifica si un refresh token existe y no ha expirado.
     *
     * @param token Token a verificar
     * @return true si el token es válido
     */
    boolean isRefreshTokenValid(String token);

    /**
     * Invalida un refresh token (lo marca como usado/expirado).
     *
     * @param token Token a invalidar
     */
    void invalidateRefreshToken(String token);

    /**
     * Invalida todos los refresh tokens de un usuario.
     *
     * @param accountId ID de la cuenta
     */
    void invalidateAllUserTokens(AccountId accountId);
}