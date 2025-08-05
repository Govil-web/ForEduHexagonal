package com.academia.application.services;

import com.academia.domain.model.aggregates.Organization;
import com.academia.domain.model.aggregates.UserAccount;
import com.academia.domain.model.entities.User;
import com.academia.domain.model.enums.AccountStatus;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.user.Email;
import com.academia.domain.model.valueobjects.user.Name;
import com.academia.domain.ports.in.commands.CreateOrganizationCommand;
import com.academia.domain.ports.in.dtos.OrganizationDetailsDTO;
import com.academia.domain.ports.out.DomainEventPublisher;
import com.academia.domain.ports.out.OrganizationRepository;
import com.academia.domain.ports.out.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOrganizationServiceImplTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CreateOrganizationServiceImpl createOrganizationService;

    private CreateOrganizationCommand validCommand;
    private Organization mockOrganization;
    private UserAccount mockUserAccount;
    private User mockUser;

    @BeforeEach
    void setUp() {
        validCommand = new CreateOrganizationCommand(
                "Universidad Tecnológica",
                "unitec",
                16,
                "Ana",
                "García",
                "admin@unitec.edu",
                "AdminPass123"
        );

        // Mock de organización guardada usando constructor de la base de código actual
        mockOrganization = new Organization(
                new OrganizationId(1L),
                "Universidad Tecnológica",
                16
        );

        // Mock de User
        mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(new AccountId(1L));
        when(mockUser.getName()).thenReturn(new Name("Ana", "García"));
        when(mockUser.getEmail()).thenReturn(new Email("admin@unitec.edu"));
        when(mockUser.getAccountStatus()).thenReturn(AccountStatus.ACTIVE);

        // Mock de UserAccount
        mockUserAccount = mock(UserAccount.class);
        when(mockUserAccount.getDomainEvents()).thenReturn(new ArrayList<>());
        when(mockUserAccount.getUser()).thenReturn(mockUser);
    }

    @Test
    @DisplayName("Debe crear una organización exitosamente con administrador inicial")
    void createOrganization_shouldSucceed_whenValidCommand() {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword123");
        when(organizationRepository.save(any(Organization.class))).thenReturn(mockOrganization);
        when(userAccountRepository.save(any(UserAccount.class))).thenReturn(mockUserAccount);

        // Act
        OrganizationDetailsDTO result = createOrganizationService.createOrganization(validCommand);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.organizationId());
        assertEquals("Universidad Tecnológica", result.name());
        assertEquals("unitec", result.subdomain());
        assertEquals(16, result.digitalConsentAge());
        assertTrue(result.isActive());
        assertNotNull(result.initialAdmin());
        assertEquals("Ana García", result.initialAdmin().fullName());
        assertEquals("admin@unitec.edu", result.initialAdmin().email());

        // Verificar interacciones
        verify(organizationRepository).save(any(Organization.class));
        verify(userAccountRepository).save(any(UserAccount.class));
        verify(domainEventPublisher).publish(any());
        verify(passwordEncoder).encode("AdminPass123");
    }

    @Test
    @DisplayName("Debe fallar cuando el comando tiene datos inválidos")
    void createOrganization_shouldFail_whenInvalidCommand() {
        // Act & Assert - El comando mismo lanza la excepción en su constructor
        assertThrows(IllegalArgumentException.class, () -> {
            new CreateOrganizationCommand(
                    "", // Nombre vacío
                    "unitec",
                    16,
                    "Ana",
                    "García",
                    "admin@unitec.edu",
                    "AdminPass123"
            );
        });
    }

    @Test
    @DisplayName("Debe hashear la contraseña del administrador")
    void createOrganization_shouldHashPassword_whenCreatingAdmin() {
        // Arrange
        when(passwordEncoder.encode("AdminPass123")).thenReturn("$2a$10$hashedPassword");
        when(organizationRepository.save(any(Organization.class))).thenReturn(mockOrganization);
        when(userAccountRepository.save(any(UserAccount.class))).thenReturn(mockUserAccount);

        // Act
        createOrganizationService.createOrganization(validCommand);

        // Assert
        verify(passwordEncoder).encode("AdminPass123");
    }
}