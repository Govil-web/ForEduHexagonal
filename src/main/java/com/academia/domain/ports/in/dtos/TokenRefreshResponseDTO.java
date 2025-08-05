// TokenRefreshResponseDTO.java
package com.academia.domain.ports.in.dtos;

import java.time.LocalDateTime;

/**
 * DTO que representa la respuesta de una renovación de token exitosa.
 */
public record TokenRefreshResponseDTO(
        String accessToken,
        String refreshToken,
        LocalDateTime accessTokenExpiresAt,
        LocalDateTime refreshTokenExpiresAt
) {}