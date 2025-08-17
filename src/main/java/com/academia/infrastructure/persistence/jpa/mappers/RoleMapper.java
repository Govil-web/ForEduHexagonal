package com.academia.infrastructure.persistence.jpa.mappers;

import com.academia.domain.model.entities.Role;
import com.academia.infrastructure.persistence.jpa.entities.PermissionJpaEntity;
import com.academia.infrastructure.persistence.jpa.entities.RoleJpaEntity;
import org.mapstruct.Mapper;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    default Role toDomain(RoleJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        Set<String> permissions = entity.getPermissions().stream()
                .map(PermissionJpaEntity::getName)
                .collect(Collectors.toSet());

        return new Role(
                entity.getId(),
                entity.getName(),
                entity.getScope(),
                permissions
        );
    }

    default Set<Role> toDomainSet(Set<RoleJpaEntity> entities) {
        if (entities == null) {
            return Set.of();
        }
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toSet());
    }
}