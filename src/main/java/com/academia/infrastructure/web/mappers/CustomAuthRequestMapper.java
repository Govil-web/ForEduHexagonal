package com.academia.infrastructure.web.mappers;

import com.academia.domain.ports.in.commands.LoginCommand;
import com.academia.domain.ports.in.commands.RefreshTokenCommand;
import com.academia.infrastructure.web.requests.LoginRequest;
import com.academia.infrastructure.web.requests.RefreshTokenRequest;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Implementación personalizada del AuthRequestMapper que maneja la versión actualizada
 * de LoginRequest y LoginCommand sin el campo organizationSubdomain.
 * 
 * Esta clase tiene la anotación @Primary para asegurar que se use en lugar de la implementación
 * generada por MapStruct.
 */
@Component
@Primary
public class CustomAuthRequestMapper implements AuthRequestMapper {

    @Override
    public LoginCommand toLoginCommand(LoginRequest request) {
        if (request == null) {
            return null;
        }
        
        return new LoginCommand(
                request.email(),
                request.password()
        );
    }

    @Override
    public RefreshTokenCommand toRefreshTokenCommand(RefreshTokenRequest request) {
        if (request == null) {
            return null;
        }
        
        return new RefreshTokenCommand(
                request.refreshToken()
        );
    }
}