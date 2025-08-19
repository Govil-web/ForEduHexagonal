package com.academia.infrastructure.persistence.jpa.repositories;
import com.academia.infrastructure.persistence.jpa.entities.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface SpringUserRepository extends JpaRepository<UserJpaEntity, java.util.UUID> {
    Optional<UserJpaEntity> findByOrganizationIdAndEmail(java.util.UUID organizationId, String email);
    boolean existsByOrganizationIdAndEmail(java.util.UUID organizationId, String email);
    /**
     * Busca usuarios del sistema (super admins) que no pertenecen a ninguna organización.
     *
     * @param email Email del usuario del sistema
     * @return Optional con la entidad si existe
     */
    Optional<UserJpaEntity> findByOrganizationIdIsNullAndEmail(String email);
}
