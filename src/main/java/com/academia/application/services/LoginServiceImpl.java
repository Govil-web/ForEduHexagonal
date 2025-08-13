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
import java.util.Set;
import java.util.stream.Collectors;

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

    @Override
    @Transactional
    public AuthenticationResponseDTO login(LoginCommand command) {
        log.info("Intento de login para email: {}", command.email());

        String identifier = command.email(); // Identificador simplificado para rate limiting

        // 1. Verificar rate limiting
        if (attemptService.isBlocked(identifier)) {
            long remainingMinutes = attemptService.getBlockTimeRemainingMinutes(identifier);
            log.warn("Login bloqueado por rate limiting: {}. Tiempo restante: {} minutos",
                    identifier, remainingMinutes);
            throw new IllegalStateException(
                    "Demasiados intentos fallidos. Intente nuevamente en " + remainingMinutes + " minutos");
        }

        try {
            // 2. Buscar organización por email
            Email email = new Email(command.email());
            OrganizationId organizationId = findOrganizationByEmail(email);
            
            // 3. Buscar organización por ID
            Organization organization = organizationRepository.findById(organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Organización no encontrada para ID: " + organizationId.getValue()));
            organizationContextService.validateOrganizationIsActive(organization);

            // 4. Buscar usuario por email dentro de la organización
            UserAccount userAccount = userAccountRepository.findByEmail(organizationId, email)
                    .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

            User user = userAccount.getUser();

            // 5. Validar contexto organizacional
            if (!organizationContextService.userBelongsToOrganization(user, organizationId)) {
                throw new IllegalArgumentException("Usuario no pertenece a esta organización");
            }

            // 6. Validar contraseña
            if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
                log.warn("Contraseña incorrecta para usuario: {}", command.email());
                attemptService.recordFailedAttempt(identifier);
                throw new IllegalArgumentException("Credenciales inválidas");
            }

            // 7. Validar estado de la cuenta
            validateAccountStatus(user);

            // 8. Limpiar intentos fallidos tras login exitoso
            attemptService.clearFailedAttempts(identifier);

            // 9. Generar tokens
            String accessToken = jwtTokenProvider.generateAccessToken(userAccount, organization.getSubdomain());
            String refreshToken = jwtTokenProvider.generateRefreshToken(userAccount);

            // 10. Guardar refresh token
            refreshTokenRepository.saveRefreshToken(
                    refreshToken,
                    user.getId(),
                    jwtTokenProvider.getRefreshTokenExpiration().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
            );

            // 11. Construir respuesta
            AuthenticationResponseDTO.UserAuthInfoDTO userInfo = buildUserAuthInfo(user, organization);

            log.info("Login exitoso para usuario: {} en organización: {}",
                    command.email(), organization.getSubdomain());

            return new AuthenticationResponseDTO(
                    accessToken,
                    refreshToken,
                    jwtTokenProvider.getAccessTokenExpiration(),
                    jwtTokenProvider.getRefreshTokenExpiration(),
                    userInfo
            );

        } catch (IllegalArgumentException | IllegalStateException e) {
            // Registrar intento fallido para errores de autenticación
            if (!"Demasiados intentos fallidos".startsWith(e.getMessage())) {
                attemptService.recordFailedAttempt(identifier);
            }
            throw e;
        }
    }

    /**
     * Busca la organización asociada a un email utilizando el índice global de emails.
     * 
     * @param email El email a buscar
     * @return El ID de la organización asociada al email
     * @throws ResourceNotFoundException si no se encuentra ninguna organización para el email
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

    private AuthenticationResponseDTO.UserAuthInfoDTO buildUserAuthInfo(User user, Organization organization) {
        Set<Role> roles = user.getRoles();
        List<String> roleNames = roles.stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        List<String> permissions = roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .distinct()
                .collect(Collectors.toList());

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
}