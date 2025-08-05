package com.academia.application.services;

import com.academia.domain.ports.in.auth.LogoutUseCase;
import com.academia.domain.ports.out.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutServiceImpl implements LogoutUseCase {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public void logout(String refreshToken) {
        log.info("Procesando logout");

        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenRepository.invalidateRefreshToken(refreshToken);
            log.info("Refresh token invalidado exitosamente");
        }
    }
}