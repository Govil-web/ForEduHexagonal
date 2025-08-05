package com.academia.infrastructure.persistence.adapters;

import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.ports.out.RefreshTokenRepository;
import com.academia.infrastructure.persistence.jpa.entities.RefreshTokenJpaEntity;
import com.academia.infrastructure.persistence.jpa.repositories.SpringRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JpaRefreshTokenRepositoryAdapter implements RefreshTokenRepository {

    private final SpringRefreshTokenRepository jpaRepository;

    @Override
    public void saveRefreshToken(String token, AccountId accountId, long expirationTime) {
        log.debug("Guardando refresh token para usuario ID: {}", accountId.getValue());

        LocalDateTime expiresAt = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(expirationTime),
                ZoneId.systemDefault()
        );

        RefreshTokenJpaEntity entity = new RefreshTokenJpaEntity(
                token,
                accountId.getValue(),
                expiresAt
        );

        jpaRepository.save(entity);
        log.debug("Refresh token guardado exitosamente");
    }

    @Override
    public Optional<AccountId> findAccountByRefreshToken(String token) {
        log.debug("Buscando cuenta por refresh token");

        return jpaRepository.findValidToken(token, LocalDateTime.now())
                .map(entity -> new AccountId(entity.getUserId()));
    }

    @Override
    public boolean isRefreshTokenValid(String token) {
        log.debug("Validando refresh token");

        return jpaRepository.findValidToken(token, LocalDateTime.now()).isPresent();
    }

    @Override
    public void invalidateRefreshToken(String token) {
        log.debug("Invalidando refresh token");

        Optional<RefreshTokenJpaEntity> tokenEntity = jpaRepository.findByToken(token);
        if (tokenEntity.isPresent()) {
            RefreshTokenJpaEntity entity = tokenEntity.get();
            entity.revoke();
            jpaRepository.save(entity);
            log.debug("Refresh token invalidado exitosamente");
        } else {
            log.warn("Refresh token no encontrado para invalidar: {}", token.substring(0, 10) + "...");
        }
    }

    @Override
    public void invalidateAllUserTokens(AccountId accountId) {
        log.debug("Invalidando todos los tokens del usuario ID: {}", accountId.getValue());

        jpaRepository.revokeAllUserTokens(accountId.getValue(), LocalDateTime.now());
        log.debug("Todos los tokens del usuario invalidados exitosamente");
    }

    /**
     * Método de utilidad para limpiar tokens expirados (puede ser llamado por un job programado).
     */
    public void cleanupExpiredTokens() {
        log.info("Limpiando tokens expirados y revocados");
        jpaRepository.deleteExpiredAndRevokedTokens(LocalDateTime.now());
    }
}