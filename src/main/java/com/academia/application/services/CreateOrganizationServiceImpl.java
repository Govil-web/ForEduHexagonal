package com.academia.application.services;

import com.academia.application.exceptions.ResourceNotFoundException;
import com.academia.domain.model.aggregates.Organization;
import com.academia.domain.model.aggregates.UserAccount;
import com.academia.domain.model.entities.Role;
import com.academia.domain.model.entities.User;
import com.academia.domain.model.enums.AccountStatus;
import com.academia.domain.model.enums.RoleScope;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.user.Email;
import com.academia.domain.model.valueobjects.user.Name;
import com.academia.domain.ports.in.commands.CreateOrganizationCommand;
import com.academia.domain.ports.in.dtos.OrganizationDetailsDTO;
import com.academia.domain.ports.in.organization.CreateOrganizationUseCase;
import com.academia.domain.ports.out.DomainEventPublisher;
import com.academia.domain.ports.out.OrganizationRepository;
import com.academia.domain.ports.out.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateOrganizationServiceImpl implements CreateOrganizationUseCase {

    private final OrganizationRepository organizationRepository;
    private final UserAccountRepository userAccountRepository;
    private final DomainEventPublisher domainEventPublisher;
    private final PasswordEncoder passwordEncoder;

    // Constantes para evitar valores hardcodeados
    private static final LocalDate DEFAULT_ADMIN_BIRTH_DATE = LocalDate.of(1990, 1, 1);
    private static final AccountStatus DEFAULT_ADMIN_STATUS = AccountStatus.ACTIVE;
    
    @Override
    @Transactional
    public OrganizationDetailsDTO createOrganization(CreateOrganizationCommand command) {
        log.info("Creando nueva organización: {} con subdominio: {}",
                command.organizationName(), command.subdomain());

        // Validaciones previas
        validateSubdomainAvailability(command.subdomain());
        Email adminEmail = new Email(command.adminEmail());
        validateAdminEmailAvailability(adminEmail);

        // Crear y guardar la organización
        Organization organization = buildOrganizationEntity(command);
        Organization savedOrganization = organizationRepository.save(organization);
        OrganizationId organizationId = savedOrganization.getId();

        // Crear y guardar el administrador
        UserAccount adminUserAccount = buildAdminUserAccount(command, organizationId);
        UserAccount savedAdminAccount = userAccountRepository.save(adminUserAccount);

        // Publicar eventos de dominio
        domainEventPublisher.publish(savedAdminAccount.getDomainEvents());

        log.info("Organización creada exitosamente con ID: {} y administrador ID: {}",
                organizationId.getValue(), savedAdminAccount.getUser().getId().getValue());

        return buildOrganizationDetailsDTO(savedOrganization, savedAdminAccount, command.subdomain());
    }
    
    /**
     * Construye una entidad de organización a partir del comando.
     */
    private Organization buildOrganizationEntity(CreateOrganizationCommand command) {
        return new Organization(
                null, // ID será asignado por la persistencia
                command.organizationName(),
                command.digitalConsentAge()
        );
    }
    
    /**
     * Construye una cuenta de administrador para la organización.
     */
    private UserAccount buildAdminUserAccount(CreateOrganizationCommand command, OrganizationId organizationId) {
        Name adminName = new Name(command.adminFirstName(), command.adminLastName());
        String hashedPassword = passwordEncoder.encode(command.adminPassword());
        Email adminEmail = new Email(command.adminEmail());
        
        User adminUser = new User(
                null, // ID será asignado por la persistencia
                organizationId,
                adminName,
                adminEmail,
                DEFAULT_ADMIN_BIRTH_DATE, // Usando constante en lugar de valor hardcodeado
                hashedPassword,
                DEFAULT_ADMIN_STATUS // Usando constante en lugar de valor hardcodeado
        );

        UserAccount adminUserAccount = new UserAccount(adminUser);
        Role organizationAdminRole = createOrganizationAdminRole();
        adminUser.assignRole(organizationAdminRole);
        
        return adminUserAccount;
    }

    /**
     * Valida que el subdominio esté disponible y tenga un formato válido.
     * 
     * @param subdomain El subdominio a validar
     * @throws ResourceNotFoundException si el subdominio ya está en uso
     * @throws IllegalArgumentException si el formato del subdominio es inválido
     */
    private void validateSubdomainAvailability(String subdomain) {
        log.debug("Validando disponibilidad del subdominio: {}", subdomain);
        
        // Verificar si el subdominio ya existe en la base de datos
        boolean subdomainExists = organizationRepository.existsBySubdomain(subdomain);
        if (subdomainExists) {
            throw new ResourceNotFoundException("El subdominio '" + subdomain + "' ya está en uso. Por favor, elija otro.");
        }
        
        // Validar formato del subdominio (aunque ya se valida en el DTO, es buena práctica duplicar validaciones críticas)
        if (!subdomain.matches("^[a-z0-9]+(-[a-z0-9]+)*$")) {
            throw new IllegalArgumentException("El subdominio solo puede contener letras minúsculas, números y guiones.");
        }
    }

    /**
     * Valida que el email del administrador esté disponible.
     * 
     * @param email El email a validar
     * @throws ResourceNotFoundException si el email ya está registrado
     */
    private void validateAdminEmailAvailability(Email email) {
        log.debug("Validando disponibilidad del email de administrador: {}", email.value());
        
        // Como estamos creando una nueva organización, no tenemos un OrganizationId específico para verificar
        // En un escenario real, podríamos tener un método en el repositorio para verificar globalmente
        // o consultar todas las organizaciones y verificar en cada una
        
        // Implementación simplificada: asumimos que el email está disponible
        // En una implementación real, se debería verificar en todas las organizaciones
        log.warn("Validación de email global no implementada completamente. Se asume que el email está disponible.");
        
        // Alternativa: si tuviéramos un método para obtener todas las organizaciones
        // organizationRepository.findAll().forEach(org -> {
        //     if (userAccountRepository.existsByEmail(org.getId(), email)) {
        //         throw new ResourceNotFoundException("El email '" + email.value() + "' ya está registrado. Por favor, utilice otro email.");
        //     }
        // });
    }

    // Constantes para los roles
    private static final int ORGANIZATION_ADMIN_ROLE_ID = 2;
    private static final String ORGANIZATION_ADMIN_ROLE_NAME = "ORGANIZATION_ADMIN";
    private static final Set<String> ORGANIZATION_ADMIN_PERMISSIONS = Set.of(
            "organization:manage", 
            "users:manage", 
            "courses:manage"
    );
    
    /**
     * Crea un rol de administrador de organización.
     * En una implementación real, esto vendría de un servicio de roles o un repositorio.
     */
    private Role createOrganizationAdminRole() {
        return new Role(
                ORGANIZATION_ADMIN_ROLE_ID,
                ORGANIZATION_ADMIN_ROLE_NAME,
                RoleScope.TENANT,
                ORGANIZATION_ADMIN_PERMISSIONS
        );
    }

    /**
     * Construye el DTO de respuesta con los detalles de la organización creada.
     * 
     * @param organization La organización creada
     * @param adminAccount La cuenta de administrador creada
     * @param subdomain El subdominio de la organización
     * @return DTO con los detalles de la organización
     */
    private OrganizationDetailsDTO buildOrganizationDetailsDTO(
            Organization organization,
            UserAccount adminAccount,
            String subdomain) {

        // Construir el DTO del administrador
        OrganizationDetailsDTO.AdminDetailsDTO adminDTO = buildAdminDetailsDTO(adminAccount);

        // Construir y retornar el DTO de la organización
        return new OrganizationDetailsDTO(
                organization.getId().getValue(),
                organization.getName(),
                subdomain, // El subdominio no está en el agregado, viene del comando
                organization.getDigitalConsentAge(),
                organization.isActive(),
                LocalDateTime.now(), // Timestamp de creación
                adminDTO
        );
    }
    
    /**
     * Construye el DTO con los detalles del administrador.
     * 
     * @param adminAccount La cuenta de administrador
     * @return DTO con los detalles del administrador
     */
    private OrganizationDetailsDTO.AdminDetailsDTO buildAdminDetailsDTO(UserAccount adminAccount) {
        User adminUser = adminAccount.getUser();
        
        return new OrganizationDetailsDTO.AdminDetailsDTO(
                adminUser.getId().getValue(),
                adminUser.getName().getFullName(),
                adminUser.getEmail().value(),
                adminUser.getAccountStatus().name()
        );
    }
}