package com.academia.infrastructure.persistence.jpa.mappers;

import com.academia.domain.model.aggregates.UserAccount;
import com.academia.domain.model.entities.User;
import com.academia.infrastructure.persistence.jpa.entities.UserJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserAccountMapper {
    UserAccountMapper INSTANCE = Mappers.getMapper(UserAccountMapper.class);

    // --- De Entidad JPA a Dominio ---
    @Mapping(target = "id.value", source = "id")
    @Mapping(target = "organizationId.value", source = "organizationId")
    // CORRECCIÓN: Mapea los campos de la entidad JPA a los campos del Value Object 'Name'
    @Mapping(target = "name.firstName", source = "firstName")
    @Mapping(target = "name.lastName", source = "lastName")
    @Mapping(target = "email.value", source = "email")
    User toDomain(UserJpaEntity entity);

    default UserAccount toAggregate(UserJpaEntity entity) {
        return new UserAccount(toDomain(entity));
    }

    // --- De Dominio a Entidad JPA ---
    @Mapping(target = "id", source = "user.id.value")
    @Mapping(target = "organizationId", source = "user.organizationId.value")
    // CORRECCIÓN: Mapea los campos del Value Object 'Name' a los campos de la entidad JPA
    @Mapping(target = "firstName", source = "user.name.firstName")
    @Mapping(target = "lastName", source = "user.name.lastName")
    @Mapping(target = "email", source = "user.email.value")
    UserJpaEntity toJpa(UserAccount aggregate);
}