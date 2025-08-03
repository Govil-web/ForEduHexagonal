package com.academia.infrastructure.persistence.jpa.repositories;

import com.academia.infrastructure.persistence.jpa.entities.OrganizationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SpringOrganizationRepository extends JpaRepository<OrganizationJpaEntity, Long> {}
