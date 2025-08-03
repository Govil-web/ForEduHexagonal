package com.academia.infrastructure.persistence.adapters;

import com.academia.domain.model.aggregates.Organization;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.ports.out.OrganizationRepository;
import com.academia.infrastructure.persistence.jpa.mappers.OrganizationMapper; // Necesitaremos este mapper
import com.academia.infrastructure.persistence.jpa.repositories.SpringOrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaOrganizationRepositoryAdapter implements OrganizationRepository {

    private final SpringOrganizationRepository jpaRepository;
    private final OrganizationMapper mapper;

    @Override
    public Organization save(Organization organization) {
        var jpaEntity = mapper.toJpa(organization);
        var savedEntity = jpaRepository.save(jpaEntity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Organization> findById(OrganizationId id) {
        return jpaRepository.findById(id.getValue()).map(mapper::toDomain);
    }
}