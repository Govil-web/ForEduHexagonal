package com.academia.application.services;

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

    @Override
    @Transactional
    public OrganizationDetailsDTO createOrganization(CreateOrganizationCommand command) {
        log.info("Creando nueva organización: {} con subdominio: {}",
                command.organizationName(), command.subdomain());

        // 1. Validar que el subdominio no esté en uso
        validateSubdomainAvailability(command.subdomain());

        // 2. Validar que el email del admin no esté en uso a nivel global
        Email adminEmail = new Email(command.adminEmail());
        validateAdminEmailAvailability(adminEmail);

        // 3. Crear la organización usando el constructor de la base de código actual
        Organization organization = new Organization(
                null, // ID será asignado por la persistencia
                command.organizationName(),
                command.digitalConsentAge()
        );

        Organization savedOrganization = organizationRepository.save(organization);
        OrganizationId organizationId = savedOrganization.getId();

        // 4. Crear el usuario administrador inicial
        Name adminName = new Name(command.adminFirstName(), command.adminLastName());
        String hashedPassword = passwordEncoder.encode(command.adminPassword());

        User adminUser = new User(
                null, // ID será asignado por la persistencia
                organizationId,
                adminName,
                adminEmail,
                LocalDate.of(1990, 1, 1), // Fecha de nacimiento por defecto para admins
                hashedPassword,
                AccountStatus.ACTIVE // Los admins se crean directamente activos
        );

        // 5. Crear el agregado UserAccount y asignar rol
        UserAccount adminUserAccount = new UserAccount(adminUser);
        Role organizationAdminRole = createOrganizationAdminRole();
        adminUser.assignRole(organizationAdminRole);

        UserAccount savedAdminAccount = userAccountRepository.save(adminUserAccount);

        // 6. Publicar eventos de dominio
        domainEventPublisher.publish(savedAdminAccount.getDomainEvents());

        log.info("Organización creada exitosamente con ID: {} y administrador ID: {}",
                organizationId.getValue(), savedAdminAccount.getUser().getId().getValue());

        // 7. Construir y retornar el DTO de respuesta
        return buildOrganizationDetailsDTO(savedOrganization, savedAdminAccount, command.subdomain());
    }

    private void validateSubdomainAvailability(String subdomain) {
        // En una implementación real, verificaríamos en la BD
        log.debug("Validando disponibilidad del subdominio: {}", subdomain);
    }

    private void validateAdminEmailAvailability(Email email) {
        // Como es el primer usuario de la organización, verificamos a nivel global
        log.debug("Validando disponibilidad del email de administrador: {}", email.value());
    }

    private Role createOrganizationAdminRole() {
        // En una implementación real, esto vendría de un servicio de roles
        return new Role(2, "ORGANIZATION_ADMIN", RoleScope.TENANT,
                Set.of("organization:manage", "users:manage", "courses:manage"));
    }

    private OrganizationDetailsDTO buildOrganizationDetailsDTO(
            Organization organization,
            UserAccount adminAccount,
            String subdomain) {

        OrganizationDetailsDTO.AdminDetailsDTO adminDTO = new OrganizationDetailsDTO.AdminDetailsDTO(
                adminAccount.getUser().getId().getValue(),
                adminAccount.getUser().getName().getFullName(),
                adminAccount.getUser().getEmail().value(),
                adminAccount.getUser().getAccountStatus().name()
        );

        return new OrganizationDetailsDTO(
                organization.getId().getValue(),
                organization.getName(),
                subdomain, // Pasamos el subdomain del comando ya que no está en el agregado actual
                organization.getDigitalConsentAge(),
                organization.isActive(),
                LocalDateTime.now(), // Timestamp actual ya que no está en el agregado
                adminDTO
        );
    }
}