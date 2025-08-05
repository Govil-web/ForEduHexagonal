package com.academia.infrastructure.persistence.jpa.mappers;

import com.academia.domain.model.aggregates.UserAccount;
import com.academia.domain.model.entities.User;
import com.academia.domain.model.enums.AccountStatus;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.user.Email;
import com.academia.domain.model.valueobjects.user.Name;
import com.academia.infrastructure.persistence.jpa.entities.UserJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface UserAccountMapper {
    UserAccountMapper INSTANCE = Mappers.getMapper(UserAccountMapper.class);

    // --- De Entidad JPA a Dominio ---
    // NOTA: User tiene campos final, usamos método custom para crear la instancia
    default User toDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        AccountId accountId = entity.getId() != null ? new AccountId(entity.getId()) : null;
        OrganizationId organizationId = entity.getOrganizationId() != null ? new OrganizationId(entity.getOrganizationId()) : null;
        Name name = new Name(entity.getFirstName(), entity.getLastName());
        Email email = new Email(entity.getEmail());

        // Crear User usando el constructor existente (sin createdAt/updatedAt)
        return new User(
                accountId,
                organizationId,
                name,
                email,
                entity.getBirthDate(),
                entity.getPasswordHash(),
                entity.getAccountStatus()
        );
    }

    default UserAccount toAggregate(UserJpaEntity entity) {
        return new UserAccount(toDomain(entity));
    }

    // --- De Dominio a Entidad JPA ---
    @Mapping(target = "id", source = "user.id.value")
    @Mapping(target = "organizationId", source = "user.organizationId.value")
    @Mapping(target = "firstName", source = "user.name.firstName")
    @Mapping(target = "lastName", source = "user.name.lastName")
    @Mapping(target = "email", source = "user.email.value")
    @Mapping(target = "passwordHash", source = "user.passwordHash")
    @Mapping(target = "birthDate", source = "user.birthDate")
    @Mapping(target = "accountStatus", source = "user.accountStatus")
    @Mapping(target = "dni", ignore = true) // Campo opcional no mapeado
    @Mapping(target = "phoneNumber", ignore = true) // Campo opcional no mapeado
    @Mapping(target = "createdAt", ignore = true) // Manejado por @PrePersist
    @Mapping(target = "updatedAt", ignore = true) // Manejado por @PreUpdate
    UserJpaEntity toJpa(UserAccount aggregate);
}