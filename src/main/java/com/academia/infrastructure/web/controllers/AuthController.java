package com.academia.infrastructure.web.controllers;

import com.academia.domain.ports.in.auth.LoginUseCase;
import com.academia.domain.ports.in.auth.LogoutUseCase;
import com.academia.domain.ports.in.auth.RefreshTokenUseCase;
import com.academia.domain.ports.in.commands.LoginCommand;
import com.academia.domain.ports.in.commands.RefreshTokenCommand;
import com.academia.domain.ports.in.dtos.AuthenticationResponseDTO;
import com.academia.domain.ports.in.dtos.TokenRefreshResponseDTO;
import com.academia.infrastructure.web.mappers.AuthRequestMapper;
import com.academia.infrastructure.web.requests.LoginRequest;
import com.academia.infrastructure.web.requests.RefreshTokenRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "API para autenticación y autorización")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final AuthRequestMapper requestMapper;

    @PostMapping("/login")
    @Operation(
            summary = "Autenticar usuario",
            description = "Autentica un usuario con email y contraseña, devuelve JWT access token y refresh token"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
            @ApiResponse(responseCode = "400", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "401", description = "Usuario o contraseña incorrectos"),
            @ApiResponse(responseCode = "403", description = "Cuenta suspendida o no puede autenticarse")
    })
    public ResponseEntity<AuthenticationResponseDTO> login(@Valid @RequestBody LoginRequest request) {
        log.info("Solicitud de login para email: {}", request.email());

        try {
            LoginCommand command = requestMapper.toLoginCommand(request);
            AuthenticationResponseDTO response = loginUseCase.login(command);

            log.info("Login exitoso para usuario: {}", request.email());
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("Error de autenticación: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            log.warn("Estado de cuenta inválido: {}", e.getMessage());
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            log.error("Error inesperado durante login", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Renovar token de acceso",
            description = "Renueva el access token usando un refresh token válido. Implementa token rotation."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token renovado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Refresh token inválido"),
            @ApiResponse(responseCode = "401", description = "Refresh token expirado")
    })
    public ResponseEntity<TokenRefreshResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.debug("Solicitud de renovación de token");

        try {
            RefreshTokenCommand command = requestMapper.toRefreshTokenCommand(request);
            TokenRefreshResponseDTO response = refreshTokenUseCase.refreshToken(command);

            log.debug("Token renovado exitosamente");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("Error al renovar token: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error inesperado al renovar token", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Cerrar sesión",
            description = "Invalida el refresh token del usuario para cerrar la sesión"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout exitoso"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<Map<String, String>> logout(@RequestBody(required = false) RefreshTokenRequest request) {
        log.info("Solicitud de logout");

        try {
            if (request != null && request.refreshToken() != null) {
                logoutUseCase.logout(request.refreshToken());
            }

            return ResponseEntity.ok(Map.of("message", "Logout exitoso"));

        } catch (Exception e) {
            log.error("Error durante logout", e);
            return ResponseEntity.ok(Map.of("message", "Logout procesado"));
        }
    }

    @GetMapping("/me")
    @Operation(
            summary = "Obtener información del usuario autenticado",
            description = "Devuelve la información del usuario actualmente autenticado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información del usuario"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            Authentication authentication,
            HttpServletRequest request
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        try {
            // Obtener información del contexto de la request
            Object userClaims = request.getAttribute("userClaims");
            Object organizationId = request.getAttribute("organizationId");
            Object organizationSubdomain = request.getAttribute("organizationSubdomain");

            Map<String, Object> userInfo = Map.of(
                    "email", authentication.getName(),
                    "authorities", authentication.getAuthorities(),
                    "organizationId", organizationId != null ? organizationId : "unknown",
                    "organizationSubdomain", organizationSubdomain != null ? organizationSubdomain : "unknown",
                    "authenticated", true
            );

            return ResponseEntity.ok(userInfo);

        } catch (Exception e) {
            log.error("Error al obtener información del usuario", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}