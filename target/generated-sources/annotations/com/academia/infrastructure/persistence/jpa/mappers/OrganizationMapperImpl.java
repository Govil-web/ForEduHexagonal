package com.academia.infrastructure.persistence.jpa.mappers;

import com.academia.domain.model.aggregates.Organization;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.infrastructure.persistence.jpa.entities.OrganizationJpaEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-03T17:56:56-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Amazon.com Inc.)"
)
@Component
public class OrganizationMapperImpl implements OrganizationMapper {

    @Override
    public Organization toDomain(OrganizationJpaEntity jpaEntity) {
        if ( jpaEntity == null ) {
            return null;
        }

        OrganizationId id = null;
        String name = null;

        id = longToOrganizationId( jpaEntity.getId() );
        name = jpaEntity.getName();

        int consentAge = 0;

        Organization organization = new Organization( id, name, consentAge );

        return organization;
    }

    @Override
    public OrganizationJpaEntity toJpa(Organization organization) {
        if ( organization == null ) {
            return null;
        }

        OrganizationJpaEntity organizationJpaEntity = new OrganizationJpaEntity();

        organizationJpaEntity.setId( organizationIdValue( organization ) );
        organizationJpaEntity.setName( organization.getName() );
        organizationJpaEntity.setActive( organization.isActive() );
        organizationJpaEntity.setDigitalConsentAge( organization.getDigitalConsentAge() );

        return organizationJpaEntity;
    }

    private Long organizationIdValue(Organization organization) {
        if ( organization == null ) {
            return null;
        }
        OrganizationId id = organization.getId();
        if ( id == null ) {
            return null;
        }
        Long value = id.getValue();
        if ( value == null ) {
            return null;
        }
        return value;
    }
}
