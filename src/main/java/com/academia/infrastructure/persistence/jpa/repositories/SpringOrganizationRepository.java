package com.academia.infrastructure.persistence.jpa.repositories;

import com.academia.infrastructure.persistence.jpa.entities.OrganizationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringOrganizationRepository extends JpaRepository<OrganizationJpaEntity, Long> {

    Optional<OrganizationJpaEntity> findBySubdomain(String subdomain);
}
