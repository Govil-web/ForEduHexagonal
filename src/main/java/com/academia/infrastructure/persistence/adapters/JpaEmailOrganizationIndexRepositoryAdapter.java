package com.academia.infrastructure.persistence.adapters;

import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.user.Email;
import com.academia.domain.ports.out.EmailOrganizationIndexRepository;
import com.academia.infrastructure.persistence.jpa.entities.EmailOrganizationIndexJpaEntity;
import com.academia.infrastructure.persistence.jpa.repositories.SpringEmailOrganizationIndexRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Adaptador que implementa el puerto EmailOrganizationIndexRepository
 * utilizando el repositorio Spring Data JPA.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class JpaEmailOrganizationIndexRepositoryAdapter implements EmailOrganizationIndexRepository {

    private final SpringEmailOrganizationIndexRepository jpaRepository;

    @Override
    public Optional<OrganizationId> findOrganizationByEmail(Email email) {
        log.debug("Buscando organización por email: {}", email.value());
        return jpaRepository.findOrganizationIdByEmail(email.value())
                .map(OrganizationId::new);
    }

    @Override
    public boolean emailExistsGlobally(Email email) {
        log.debug("Verificando si el email existe globalmente: {}", email.value());
        return jpaRepository.existsByEmail(email.value());
    }

    @Override
    public void registerEmail(Email email, OrganizationId organizationId, Long userId) {
        log.debug("Registrando email en índice global: {} para organización: {} y usuario: {}", 
                email.value(), organizationId.getValue(), userId);
        
        // Verificar si el email ya existe
        if (jpaRepository.existsByEmail(email.value())) {
            log.warn("El email {} ya existe en el índice global. No se registrará nuevamente.", email.value());
            return;
        }
        
        EmailOrganizationIndexJpaEntity entity = new EmailOrganizationIndexJpaEntity(
                email.value(),
                organizationId.getValue(),
                userId
        );
        
        jpaRepository.save(entity);
        log.debug("Email registrado exitosamente en el índice global");
    }
}