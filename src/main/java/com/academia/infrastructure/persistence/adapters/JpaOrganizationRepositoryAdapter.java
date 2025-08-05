package com.academia.infrastructure.persistence.adapters;

import com.academia.domain.model.aggregates.Organization;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.ports.out.OrganizationRepository;
import com.academia.infrastructure.persistence.jpa.entities.OrganizationJpaEntity;
import com.academia.infrastructure.persistence.jpa.mappers.OrganizationMapper;
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
        // Convertir dominio a JPA usando el mapper
        OrganizationJpaEntity jpaEntity = mapper.toJpa(organization);

        // Guardar en la base de datos
        OrganizationJpaEntity savedEntity = jpaRepository.save(jpaEntity);

        // Convertir de vuelta a dominio
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Organization> findById(OrganizationId id) {
        return jpaRepository.findById(id.getValue()).map(mapper::toDomain);
    }

    @Override
    public Optional<Organization> findBySubdomain(String subdomain) {
        return jpaRepository.findBySubdomain(subdomain).map(mapper::toDomain);
    }

    @Override
    public boolean existsBySubdomain(String subdomain) {
        return false;
    }

    private String generateSubdomainFromName(String name) {
        if (name == null) return "org";
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-")
                .substring(0, Math.min(name.length(), 20));
    }
}