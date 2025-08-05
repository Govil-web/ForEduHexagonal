package com.academia.infrastructure.persistence.jpa.repositories;

import com.academia.infrastructure.persistence.jpa.entities.RefreshTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SpringRefreshTokenRepository extends JpaRepository<RefreshTokenJpaEntity, Long> {

    /**
     * Busca un refresh token por su valor.
     */
    Optional<RefreshTokenJpaEntity> findByToken(String token);

    /**
     * Busca un refresh token válido (no revocado y no expirado).
     */
    @Query("SELECT rt FROM RefreshTokenJpaEntity rt WHERE rt.token = :token AND rt.isRevoked = false AND rt.expiresAt > :now")
    Optional<RefreshTokenJpaEntity> findValidToken(@Param("token") String token, @Param("now") LocalDateTime now);

    /**
     * Revoca todos los tokens de un usuario.
     */
    @Modifying
    @Query("UPDATE RefreshTokenJpaEntity rt SET rt.isRevoked = true, rt.revokedAt = :now WHERE rt.userId = :userId AND rt.isRevoked = false")
    void revokeAllUserTokens(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    /**
     * Elimina tokens expirados para limpieza.
     */
    @Modifying
    @Query("DELETE FROM RefreshTokenJpaEntity rt WHERE rt.expiresAt < :now OR rt.isRevoked = true")
    void deleteExpiredAndRevokedTokens(@Param("now") LocalDateTime now);

    /**
     * Cuenta tokens activos de un usuario.
     */
    @Query("SELECT COUNT(rt) FROM RefreshTokenJpaEntity rt WHERE rt.userId = :userId AND rt.isRevoked = false AND rt.expiresAt > :now")
    long countActiveTokensForUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}