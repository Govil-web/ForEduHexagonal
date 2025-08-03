package com.academia.infrastructure.persistence.jpa.mappers;

import com.academia.domain.model.aggregates.Organization;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.infrastructure.persistence.jpa.entities.OrganizationJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {

    // --- De Entidad JPA a Dominio ---
    @Mapping(source = "id", target = "id", qualifiedByName = "longToOrganizationId")
    Organization toDomain(OrganizationJpaEntity jpaEntity);

    // --- De Dominio a Entidad JPA ---
    @Mapping(source = "id.value", target = "id")
    OrganizationJpaEntity toJpa(Organization organization);

    // --- Métodos Helper para la conversión ---
    @Named("longToOrganizationId")
    default OrganizationId longToOrganizationId(Long id) {
        return id == null ? null : new OrganizationId(id);
    }
}