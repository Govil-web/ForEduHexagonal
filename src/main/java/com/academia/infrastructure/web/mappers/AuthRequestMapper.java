package com.academia.infrastructure.web.mappers;

import com.academia.domain.ports.in.commands.LoginCommand;
import com.academia.domain.ports.in.commands.RefreshTokenCommand;
import com.academia.infrastructure.web.requests.LoginRequest;
import com.academia.infrastructure.web.requests.RefreshTokenRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

/**
 * Mapper responsable de convertir DTOs de la capa web a comandos de dominio para autenticación.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthRequestMapper {

    AuthRequestMapper INSTANCE = Mappers.getMapper(AuthRequestMapper.class);

    /**
     * Convierte el DTO de login HTTP en un Comando de dominio.
     */
    LoginCommand toLoginCommand(LoginRequest request);

    /**
     * Convierte el DTO de refresh token HTTP en un Comando de dominio.
     */
    RefreshTokenCommand toRefreshTokenCommand(RefreshTokenRequest request);
}