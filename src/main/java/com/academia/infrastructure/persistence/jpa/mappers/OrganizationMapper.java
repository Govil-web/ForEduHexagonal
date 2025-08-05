package com.academia.infrastructure.persistence.jpa.mappers;

import com.academia.domain.model.aggregates.Organization;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.infrastructure.persistence.jpa.entities.OrganizationJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper responsable de convertir entre el agregado Organization del dominio
 * y la entidad JPA OrganizationJpaEntity.
 */
@Mapper(componentModel = "spring")
public interface OrganizationMapper {

    // --- De Entidad JPA a Dominio ---
    default Organization toDomain(OrganizationJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        OrganizationId orgId = jpaEntity.getId() != null ? new OrganizationId(jpaEntity.getId()) : null;

        // Usar el constructor extendido que incluye subdomain
        return new Organization(
                orgId,
                jpaEntity.getName(),
                jpaEntity.getSubdomain(),
                jpaEntity.getDigitalConsentAge()
        );
    }

    // --- De Dominio a Entidad JPA ---
    @Mapping(source = "id.value", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "subdomain", target = "subdomain") // CORREGIDO: ahora mapea subdomain
    @Mapping(source = "digitalConsentAge", target = "digitalConsentAge")
    //@Mapping(source = "isActive", target = "isActive")
    @Mapping(target = "uuid", ignore = true) // Se genera automáticamente
    @Mapping(target = "createdAt", ignore = true) // Manejado por @PrePersist
    @Mapping(target = "updatedAt", ignore = true) // Manejado por @PreUpdate
    OrganizationJpaEntity toJpa(Organization organization);
}