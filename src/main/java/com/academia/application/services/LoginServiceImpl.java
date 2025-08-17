package com.academia.application.services;

import com.academia.application.exceptions.ResourceNotFoundException;
import com.academia.domain.model.aggregates.Organization;
import com.academia.domain.model.aggregates.UserAccount;
import com.academia.domain.model.entities.Role;
import com.academia.domain.model.entities.User;
import com.academia.domain.model.enums.AccountStatus;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.user.Email;
import com.academia.domain.ports.in.auth.LoginUseCase;
import com.academia.domain.ports.in.commands.LoginCommand;
import com.academia.domain.ports.in.dtos.AuthenticationResponseDTO;
import com.academia.domain.ports.out.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginServiceImpl implements LoginUseCase {

    private final OrganizationRepository organizationRepository;
    private final UserAccountRepository userAccountRepository;
    private final EmailOrganizationIndexRepository emailIndexRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationAttemptService attemptService;
    private final OrganizationContextService organizationContextService;
    private final SecurityAuditService securityAuditService;

    @Override
    @Transactional
    public AuthenticationResponseDTO login(LoginCommand command) {
        log.info("Intento de login para email: {}", command.email());

        String identifier = command.email();

        // 1. Verificar rate limiting
        if (attemptService.isBlocked(identifier)) {
            long remainingMinutes = attemptService.getBlockTimeRemainingMinutes(identifier);
            log.warn("Login bloqueado por rate limiting: {}. Tiempo restante: {} minutos",
                    identifier, remainingMinutes);
            throw new IllegalStateException(
                    "Demasiados intentos fallidos. Intente nuevamente en " + remainingMinutes + " minutos");
        }

        try {
            Email email = new Email(command.email());

            // 2. NUEVO: Intentar login como usuario del sistema (superadmin) primero
            Optional<UserAccount> systemUserOpt = userAccountRepository.findSystemUserByEmail(email);
            if (systemUserOpt.isPresent()) {
                log.debug("Email {} corresponde a un usuario del sistema", email.value());
                return authenticateSystemUser(systemUserOpt.get(), command.password(), identifier);
            }

            // 3. Si no es usuario del sistema, continuar con flujo normal de organización
            log.debug("Email {} no es de usuario del sistema, buscando organización", email.value());
            return authenticateOrganizationUser(email, command.password(), identifier);

        } catch (IllegalArgumentException | IllegalStateException e) {
            // Registrar intento fallido para errores de autenticación
            if (!e.getMessage().startsWith("Demasiados intentos fallidos")) {
                attemptService.recordFailedAttempt(identifier);
                
                // Auditar login fallido
                securityAuditService.logLoginFailure(
                    command.email(),
                    getClientIpAddress(),
                    getUserAgent(),
                    e.getMessage()
                );
            }
            throw e;
        }
    }

    /**
     * Autentica usuarios del sistema (super admins).
     */
    private AuthenticationResponseDTO authenticateSystemUser(UserAccount userAccount, String password, String identifier) {
        log.debug("Autenticando usuario del sistema");

        User user = userAccount.getUser();

        // Validar contraseña
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            log.warn("Contraseña incorrecta para usuario del sistema: {}", user.getEmail().value());
            attemptService.recordFailedAttempt(identifier);
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        // Validar estado de la cuenta
        validateAccountStatus(user);

        // Limpiar intentos fallidos tras login exitoso
        attemptService.clearFailedAttempts(identifier);

        // Generar tokens (SIN información de organización)
        String accessToken = jwtTokenProvider.generateAccessToken(userAccount, "system"); // Subdomain especial para sistema
        String refreshToken = jwtTokenProvider.generateRefreshToken(userAccount);

        // Guardar refresh token
        refreshTokenRepository.saveRefreshToken(
                refreshToken,
                user.getId(),
                jwtTokenProvider.getRefreshTokenExpiration().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        );

        // Construir respuesta (SIN información de organización)
        AuthenticationResponseDTO.UserAuthInfoDTO userInfo = buildSystemUserAuthInfo(user);

        // Auditar login exitoso
        securityAuditService.logLoginSuccess(
            user.getId(),
            user.getOrganizationId(),
            user.getEmail().value(),
            getClientIpAddress(),
            getUserAgent()
        );

        log.info("Login exitoso para usuario del sistema: {}", user.getEmail().value());

        return new AuthenticationResponseDTO(
                accessToken,
                refreshToken,
                jwtTokenProvider.getAccessTokenExpiration(),
                jwtTokenProvider.getRefreshTokenExpiration(),
                userInfo
        );
    }

    /**
     * Autentíca usuarios de organizaciones (flujo original).
     */
    private AuthenticationResponseDTO authenticateOrganizationUser(Email email, String password, String identifier) {
        // Buscar organización por email
        OrganizationId organizationId = findOrganizationByEmail(email);

        // Buscar organización por ID
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organización no encontrada para ID: " + organizationId.getValue()));
        organizationContextService.validateOrganizationIsActive(organization);

        // Buscar usuario por email dentro de la organización
        UserAccount userAccount = userAccountRepository.findByEmail(organizationId, email)
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

        User user = userAccount.getUser();

        // Validar contexto organizacional
        if (!organizationContextService.userBelongsToOrganization(user, organizationId)) {
            throw new IllegalArgumentException("Usuario no pertenece a esta organización");
        }

        // Validar contraseña
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            log.warn("Contraseña incorrecta para usuario: {}", email.value());
            attemptService.recordFailedAttempt(identifier);
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        // Validar estado de la cuenta
        validateAccountStatus(user);

        // Limpiar intentos fallidos tras login exitoso
        attemptService.clearFailedAttempts(identifier);

        // Generar tokens
        String accessToken = jwtTokenProvider.generateAccessToken(userAccount, organization.getSubdomain());
        String refreshToken = jwtTokenProvider.generateRefreshToken(userAccount);

        // Guardar refresh token
        refreshTokenRepository.saveRefreshToken(
                refreshToken,
                user.getId(),
                jwtTokenProvider.getRefreshTokenExpiration().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        );

        // Construir respuesta
        AuthenticationResponseDTO.UserAuthInfoDTO userInfo = buildUserAuthInfo(user, organization);

        // Auditar login exitoso
        securityAuditService.logLoginSuccess(
            user.getId(),
            organizationId,
            user.getEmail().value(),
            getClientIpAddress(),
            getUserAgent()
        );

        log.info("Login exitoso para usuario: {} en organización: {}",
                email.value(), organization.getSubdomain());

        return new AuthenticationResponseDTO(
                accessToken,
                refreshToken,
                jwtTokenProvider.getAccessTokenExpiration(),
                jwtTokenProvider.getRefreshTokenExpiration(),
                userInfo
        );
    }

    /**
     * Busca la organización asociada a un email utilizando el índice global de emails.
     */
    private OrganizationId findOrganizationByEmail(Email email) {
        log.debug("Buscando organización por email: {}", email.value());
        return emailIndexRepository.findOrganizationByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró ninguna organización para el email: " + email.value()));
    }

    private void validateAccountStatus(User user) {
        AccountStatus status = user.getAccountStatus();

        switch (status) {
            case ACTIVE -> { /* OK, puede autenticarse */ }
            case TUTOR_MANAGED -> {
                log.warn("Intento de login de cuenta TUTOR_MANAGED: {}", user.getEmail().value());
                throw new IllegalStateException("Esta cuenta está gestionada por un tutor y no puede iniciar sesión directamente");
            }
            case PENDING_VERIFICATION -> {
                log.warn("Intento de login de cuenta sin verificar: {}", user.getEmail().value());
                throw new IllegalStateException("La cuenta debe ser verificada antes de iniciar sesión");
            }
            case SUSPENDED -> {
                log.warn("Intento de login de cuenta suspendida: {}", user.getEmail().value());
                throw new IllegalStateException("La cuenta ha sido suspendida. Contacte al administrador");
            }
            case DEACTIVATED -> {
                log.warn("Intento de login de cuenta desactivada: {}", user.getEmail().value());
                throw new IllegalStateException("La cuenta ha sido desactivada");
            }
            default -> throw new IllegalStateException("Estado de cuenta no reconocido: " + status);
        }
    }

    /**
     * Construye información de autenticación para usuarios del sistema.
     */
    private AuthenticationResponseDTO.UserAuthInfoDTO buildSystemUserAuthInfo(User user) {
        Set<Role> roles = user.getRoles();
        List<String> roleNames = roles.stream()
                .map(Role::getName)
                .toList();

        List<String> permissions = roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .distinct()
                .toList();

        // Para usuarios del sistema, NO hay información de organización
        return new AuthenticationResponseDTO.UserAuthInfoDTO(
                user.getId().getValue(),
                user.getName().getFullName(),
                user.getEmail().value(),
                user.getAccountStatus().name(),
                null, // Sin organización para usuarios del sistema
                roleNames,
                permissions
        );
    }

    /**
     * Construye información de autenticación para usuarios de organizaciones.
     */
    private AuthenticationResponseDTO.UserAuthInfoDTO buildUserAuthInfo(User user, Organization organization) {
        Set<Role> roles = user.getRoles();
        List<String> roleNames = roles.stream()
                .map(Role::getName)
                .toList();

        List<String> permissions = roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .distinct()
                .toList();

        AuthenticationResponseDTO.OrganizationInfoDTO orgInfo =
                new AuthenticationResponseDTO.OrganizationInfoDTO(
                        organization.getId().getValue(),
                        organization.getName(),
                        organization.getSubdomain()
                );

        return new AuthenticationResponseDTO.UserAuthInfoDTO(
                user.getId().getValue(),
                user.getName().getFullName(),
                user.getEmail().value(),
                user.getAccountStatus().name(),
                orgInfo,
                roleNames,
                permissions
        );
    }

    /**
     * Obtiene la dirección IP del cliente desde el contexto de la request.
     */
    private String getClientIpAddress() {
        // TODO: Implementar extracción de IP desde HttpServletRequest
        // Por ahora retornamos un placeholder
        return "unknown";
    }

    /**
     * Obtiene el User-Agent del cliente desde el contexto de la request.
     */
    private String getUserAgent() {
        // TODO: Implementar extracción de User-Agent desde HttpServletRequest
        // Por ahora retornamos un placeholder
        return "unknown";
    }
}