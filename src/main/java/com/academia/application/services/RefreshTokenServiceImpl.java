package com.academia.application.services;

import com.academia.domain.model.aggregates.Organization;
import com.academia.domain.model.aggregates.UserAccount;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.ports.in.auth.RefreshTokenUseCase;
import com.academia.domain.ports.in.commands.RefreshTokenCommand;
import com.academia.domain.ports.in.dtos.TokenRefreshResponseDTO;
import com.academia.domain.ports.out.JwtTokenProvider;
import com.academia.domain.ports.out.OrganizationRepository;
import com.academia.domain.ports.out.RefreshTokenRepository;
import com.academia.domain.ports.out.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserAccountRepository userAccountRepository;
    private final OrganizationRepository organizationRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public TokenRefreshResponseDTO refreshToken(RefreshTokenCommand command) {
        log.debug("Renovando token con refresh token");

        // 1. Validar que el refresh token existe y es válido
        if (!refreshTokenRepository.isRefreshTokenValid(command.refreshToken())) {
            log.warn("Refresh token inválido o expirado");
            throw new IllegalArgumentException("Refresh token inválido o expirado");
        }

        // 2. Obtener la cuenta asociada al refresh token
        AccountId accountId = refreshTokenRepository.findAccountByRefreshToken(command.refreshToken())
                .orElseThrow(() -> new IllegalArgumentException("Refresh token no encontrado"));

        UserAccount userAccount = userAccountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // 3. Invalidar el refresh token anterior (token rotation)
        refreshTokenRepository.invalidateRefreshToken(command.refreshToken());

        // 4. Generar nuevos tokens
        // Obtener el subdominio de la organización del usuario
        Organization organization = organizationRepository.findById(userAccount.getUser().getOrganizationId())
                .orElseThrow(() -> new IllegalArgumentException("Organización del usuario no encontrada"));

        String newAccessToken = jwtTokenProvider.generateAccessToken(userAccount, organization.getSubdomain());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userAccount);

        // 5. Guardar el nuevo refresh token
        refreshTokenRepository.saveRefreshToken(
                newRefreshToken,
                accountId,
                jwtTokenProvider.getRefreshTokenExpiration().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        );

        log.info("Token renovado exitosamente para usuario ID: {}", accountId.getValue());

        return new TokenRefreshResponseDTO(
                newAccessToken,
                newRefreshToken,
                jwtTokenProvider.getAccessTokenExpiration(),
                jwtTokenProvider.getRefreshTokenExpiration()
        );
    }
}