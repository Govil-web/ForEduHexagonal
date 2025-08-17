package com.academia.domain.ports.out;
import com.academia.domain.model.aggregates.UserAccount;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.user.Email;
import java.util.Optional;

public interface UserAccountRepository {
    UserAccount save(UserAccount userAccount);
    Optional<UserAccount> findById(AccountId id);
    Optional<UserAccount> findByEmail(OrganizationId organizationId, Email email);
    boolean existsByEmail(OrganizationId organizationId, Email email);
    /**
     * Busca un usuario del sistema (super admin) por email.
     * Para usuarios que NO pertenecen a ninguna organización (organization_id IS NULL).
     *
     * @param email Email del usuario del sistema
     * @return Optional con el UserAccount si existe
     */
    Optional<UserAccount> findSystemUserByEmail(Email email);
}
