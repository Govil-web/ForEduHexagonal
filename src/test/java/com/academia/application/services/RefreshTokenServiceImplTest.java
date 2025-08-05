package com.academia.application.services;

import com.academia.domain.model.aggregates.UserAccount;
import com.academia.domain.model.entities.User;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.ports.in.commands.RefreshTokenCommand;
import com.academia.domain.ports.in.dtos.TokenRefreshResponseDTO;
import com.academia.domain.ports.out.JwtTokenProvider;
import com.academia.domain.ports.out.RefreshTokenRepository;
import com.academia.domain.ports.out.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    private RefreshTokenCommand validCommand;
    private UserAccount mockUserAccount;

    @BeforeEach
    void setUp() {
        validCommand = new RefreshTokenCommand("valid.refresh.token");

        mockUserAccount = mock(UserAccount.class);
        User mockUser = mock(User.class);
        when(mockUserAccount.getUser()).thenReturn(mockUser);
    }

    @Test
    @DisplayName("Debe renovar token exitosamente con refresh token válido")
    void refreshToken_shouldSucceed_whenRefreshTokenIsValid() {
        // Arrange
        AccountId accountId = new AccountId(1L);

        when(refreshTokenRepository.isRefreshTokenValid(anyString())).thenReturn(true);
        when(refreshTokenRepository.findAccountByRefreshToken(anyString())).thenReturn(Optional.of(accountId));
        when(userAccountRepository.findById(any(AccountId.class))).thenReturn(Optional.of(mockUserAccount));
        when(jwtTokenProvider.generateAccessToken(any(UserAccount.class), anyString()))
                .thenReturn("new.access.token");
        when(jwtTokenProvider.generateRefreshToken(any(UserAccount.class)))
                .thenReturn("new.refresh.token");
        when(jwtTokenProvider.getAccessTokenExpiration()).thenReturn(LocalDateTime.now().plusMinutes(15));
        when(jwtTokenProvider.getRefreshTokenExpiration()).thenReturn(LocalDateTime.now().plusDays(7));

        // Act
        TokenRefreshResponseDTO result = refreshTokenService.refreshToken(validCommand);

        // Assert
        assertNotNull(result);
        assertEquals("new.access.token", result.accessToken());
        assertEquals("new.refresh.token", result.refreshToken());

        // Verificar que se invalidó el token anterior
        verify(refreshTokenRepository).invalidateRefreshToken(validCommand.refreshToken());

        // Verificar que se guardó el nuevo refresh token
        verify(refreshTokenRepository).saveRefreshToken(
                eq("new.refresh.token"),
                eq(accountId),
                anyLong()
        );
    }

    @Test
    @DisplayName("Debe fallar cuando el refresh token es inválido")
    void refreshToken_shouldFail_whenRefreshTokenIsInvalid() {
        // Arrange
        when(refreshTokenRepository.isRefreshTokenValid(anyString())).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            refreshTokenService.refreshToken(validCommand);
        });

        // Verificar que no se intentó generar nuevos tokens
        verify(jwtTokenProvider, never()).generateAccessToken(any(), any());
        verify(jwtTokenProvider, never()).generateRefreshToken(any());
    }

    @Test
    @DisplayName("Debe fallar cuando la cuenta no existe")
    void refreshToken_shouldFail_whenAccountNotFound() {
        // Arrange
        AccountId accountId = new AccountId(1L);

        when(refreshTokenRepository.isRefreshTokenValid(anyString())).thenReturn(true);
        when(refreshTokenRepository.findAccountByRefreshToken(anyString())).thenReturn(Optional.of(accountId));
        when(userAccountRepository.findById(any(AccountId.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            refreshTokenService.refreshToken(validCommand);
        });
    }
}
