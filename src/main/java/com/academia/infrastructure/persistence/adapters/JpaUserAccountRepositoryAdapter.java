package com.academia.infrastructure.persistence.adapters;

import com.academia.domain.model.aggregates.UserAccount;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.user.Email;
import com.academia.domain.ports.out.UserAccountRepository;
import com.academia.infrastructure.persistence.jpa.entities.UserJpaEntity;
import com.academia.infrastructure.persistence.jpa.mappers.UserAccountMapper;
import com.academia.infrastructure.persistence.jpa.repositories.SpringUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // <-- Anotación clave que lo convierte en un Bean
@RequiredArgsConstructor
public class JpaUserAccountRepositoryAdapter implements UserAccountRepository {

    private final SpringUserRepository jpaRepository;
    private final UserAccountMapper mapper;

    @Override
    public UserAccount save(UserAccount userAccount) {
        UserJpaEntity jpaEntity = mapper.toJpa(userAccount);
        UserJpaEntity savedEntity = jpaRepository.save(jpaEntity);
        // Es importante re-mapear la respuesta para obtener el ID generado
        return mapper.toAggregate(savedEntity);
    }

    @Override
    public Optional<UserAccount> findById(AccountId accountId) {
        return jpaRepository.findById(accountId.getValue()).map(mapper::toAggregate);
    }

    @Override
    public Optional<UserAccount> findByEmail(OrganizationId organizationId, Email email) {
        return jpaRepository.findByOrganizationIdAndEmail(organizationId.getValue(), email.value())
                .map(mapper::toAggregate);
    }

    @Override
    public boolean existsByEmail(OrganizationId organizationId, Email email) {
        return jpaRepository.existsByOrganizationIdAndEmail(organizationId.getValue(), email.value());
    }
}
