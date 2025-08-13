package com.academia.application.services;

import com.academia.application.exceptions.ResourceNotFoundException;
import com.academia.domain.model.aggregates.Organization;
import com.academia.domain.model.aggregates.UserAccount;
import com.academia.domain.model.entities.Role;
import com.academia.domain.model.entities.User;
import com.academia.domain.model.enums.AccountStatus;
import com.academia.domain.model.enums.RoleScope;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.user.Email;
import com.academia.domain.model.valueobjects.user.Name;
import com.academia.domain.ports.in.commands.LoginCommand;
import com.academia.domain.ports.in.dtos.AuthenticationResponseDTO;
import com.academia.domain.ports.out.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceImplTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private UserAccountRepository userAccountRepository;
    
    @Mock
    private EmailOrganizationIndexRepository emailIndexRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private AuthenticationAttemptService attemptService;
    
    @Mock
    private OrganizationContextService organizationContextService;

    @InjectMocks
    private LoginServiceImpl loginService;

    private LoginCommand validCommand;
    private Organization mockOrganization;
    private UserAccount mockUserAccount;
    private User mockUser;
    private OrganizationId organizationId;
    private Email userEmail;

    @BeforeEach
    void setUp() {
        // Comando de login actualizado sin organizationSubdomain
        validCommand = new LoginCommand(
                "admin@unifuturo.edu",
                "AdminPass123"
        );
        
        organizationId = new OrganizationId(1L);
        userEmail = new Email("admin@unifuturo.edu");

        // Mock de organización
        mockOrganization = new Organization(
                organizationId,
                "Universidad del Futuro",
                "unifuturo",
                16
        );

        // Mock de usuario con rol
        Role adminRole = new Role(2, "ORGANIZATION_ADMIN", RoleScope.TENANT,
                Set.of("organization:manage", "users:manage"));

        mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(new AccountId(1L));
        when(mockUser.getOrganizationId()).thenReturn(organizationId);
        when(mockUser.getName()).thenReturn(new Name("Admin", "User"));
        when(mockUser.getEmail()).thenReturn(userEmail);
        when(mockUser.getPasswordHash()).thenReturn("hashedPassword123");
        when(mockUser.getAccountStatus()).thenReturn(AccountStatus.ACTIVE);
        when(mockUser.getRoles()).thenReturn(Set.of(adminRole));

        mockUserAccount = mock(UserAccount.class);
        when(mockUserAccount.getUser()).thenReturn(mockUser);
        
        // Mock para el servicio de contexto de organización
        when(organizationContextService.userBelongsToOrganization(any(User.class), any(OrganizationId.class)))
            .thenReturn(true);
        // validateOrganizationIsActive es void, no necesita thenReturn
        doNothing().when(organizationContextService).validateOrganizationIsActive(any(Organization.class));
    }

    @Test
    @DisplayName("Debe autenticar exitosamente con credenciales válidas")
    void login_shouldSucceed_whenCredentialsAreValid() {
        // Arrange
        // Mock para el índice de emails
        when(emailIndexRepository.findOrganizationByEmail(any(Email.class)))
                .thenReturn(Optional.of(organizationId));
                
        // Mock para la organización
        when(organizationRepository.findById(eq(organizationId)))
                .thenReturn(Optional.of(mockOrganization));
                
        // Mock para el usuario
        when(userAccountRepository.findByEmail(eq(organizationId), any(Email.class)))
                .thenReturn(Optional.of(mockUserAccount));
                
        // Mock para la validación de contraseña
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        
        // Mock para rate limiting
        when(attemptService.isBlocked(anyString())).thenReturn(false);
        doNothing().when(attemptService).clearFailedAttempts(anyString());
        
        // Mock para generación de tokens
        when(jwtTokenProvider.generateAccessToken(any(UserAccount.class), anyString()))
                .thenReturn("access.token.here");
        when(jwtTokenProvider.generateRefreshToken(any(UserAccount.class)))
                .thenReturn("refresh.token.here");
        when(jwtTokenProvider.getAccessTokenExpiration()).thenReturn(LocalDateTime.now().plusMinutes(15));
        when(jwtTokenProvider.getRefreshTokenExpiration()).thenReturn(LocalDateTime.now().plusDays(7));
        doNothing().when(refreshTokenRepository).saveRefreshToken(anyString(), any(AccountId.class), anyLong());
        
        // Mock para obtener el subdominio de la organización
        when(mockOrganization.getSubdomain()).thenReturn("unifuturo");

        // Act
        AuthenticationResponseDTO response = loginService.login(validCommand);

        // Assert
        assertNotNull(response);
        assertEquals("access.token.here", response.accessToken());
        assertEquals("refresh.token.here", response.refreshToken());
        
        // Verificar que se llamaron los métodos esperados
        verify(emailIndexRepository).findOrganizationByEmail(any(Email.class));
        verify(organizationRepository).findById(eq(organizationId));
        verify(userAccountRepository).findByEmail(eq(organizationId), any(Email.class));
        verify(passwordEncoder).matches(anyString(), anyString());
        verify(jwtTokenProvider).generateAccessToken(any(UserAccount.class), eq("unifuturo"));
    }

    @Test
    @DisplayName("Debe fallar cuando la contraseña es incorrecta")
    void login_shouldFail_whenPasswordIsIncorrect() {
        // Arrange
        // Mock para el índice de emails
        when(emailIndexRepository.findOrganizationByEmail(any(Email.class)))
                .thenReturn(Optional.of(organizationId));
                
        // Mock para la organización
        when(organizationRepository.findById(eq(organizationId)))
                .thenReturn(Optional.of(mockOrganization));
                
        // Mock para el usuario
        when(userAccountRepository.findByEmail(eq(organizationId), any(Email.class)))
                .thenReturn(Optional.of(mockUserAccount));
                
        // Mock para la validación de contraseña (incorrecta)
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        
        // Mock para rate limiting
        when(attemptService.isBlocked(anyString())).thenReturn(false);
        doNothing().when(attemptService).recordFailedAttempt(anyString());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loginService.login(validCommand);
        });
        
        assertEquals("Credenciales inválidas", exception.getMessage());
        verify(attemptService).recordFailedAttempt(anyString());
    }

    @Test
    @DisplayName("Debe fallar cuando la cuenta está gestionada por tutor")
    void login_shouldFail_whenAccountIsTutorManaged() {
        // Arrange
        // Cambiar el estado de la cuenta a TUTOR_MANAGED
        when(mockUser.getAccountStatus()).thenReturn(AccountStatus.TUTOR_MANAGED);
        
        // Mock para el índice de emails
        when(emailIndexRepository.findOrganizationByEmail(any(Email.class)))
                .thenReturn(Optional.of(organizationId));
                
        // Mock para la organización
        when(organizationRepository.findById(eq(organizationId)))
                .thenReturn(Optional.of(mockOrganization));
                
        // Mock para el usuario
        when(userAccountRepository.findByEmail(eq(organizationId), any(Email.class)))
                .thenReturn(Optional.of(mockUserAccount));
                
        // Mock para la validación de contraseña
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        
        // Mock para rate limiting
        when(attemptService.isBlocked(anyString())).thenReturn(false);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            loginService.login(validCommand);
        });
        
        assertTrue(exception.getMessage().contains("gestionada por un tutor"));
    }

    @Test
    @DisplayName("Debe fallar cuando la cuenta está suspendida")
    void login_shouldFail_whenAccountIsSuspended() {
        // Arrange
        // Cambiar el estado de la cuenta a SUSPENDED
        when(mockUser.getAccountStatus()).thenReturn(AccountStatus.SUSPENDED);
        
        // Mock para el índice de emails
        when(emailIndexRepository.findOrganizationByEmail(any(Email.class)))
                .thenReturn(Optional.of(organizationId));
                
        // Mock para la organización
        when(organizationRepository.findById(eq(organizationId)))
                .thenReturn(Optional.of(mockOrganization));
                
        // Mock para el usuario
        when(userAccountRepository.findByEmail(eq(organizationId), any(Email.class)))
                .thenReturn(Optional.of(mockUserAccount));
                
        // Mock para la validación de contraseña
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        
        // Mock para rate limiting
        when(attemptService.isBlocked(anyString())).thenReturn(false);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            loginService.login(validCommand);
        });
        
        assertTrue(exception.getMessage().contains("suspendida"));
    }
    
    @Test
    @DisplayName("Debe fallar cuando el email no existe en el índice global")
    void login_shouldFail_whenEmailNotFoundInGlobalIndex() {
        // Arrange
        // Mock para el índice de emails (email no encontrado)
        when(emailIndexRepository.findOrganizationByEmail(any(Email.class)))
                .thenReturn(Optional.empty());
        
        // Mock para rate limiting
        when(attemptService.isBlocked(anyString())).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            loginService.login(validCommand);
        });
        
        assertTrue(exception.getMessage().contains("No se encontró ninguna organización para el email"));
    }
}
