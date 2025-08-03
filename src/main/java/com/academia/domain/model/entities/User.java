package com.academia.domain.model.entities;

import com.academia.domain.model.enums.AccountStatus;
import com.academia.domain.model.enums.RoleScope;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.user.DNI;
import com.academia.domain.model.valueobjects.user.Email;
import com.academia.domain.model.valueobjects.user.Name;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashSet;
import java.util.Set;

@Getter
public class User {
    private AccountId id;
    private final OrganizationId organizationId;
    private final Name name;
    private DNI dni;
    private final Email email;
    private String passwordHash;
    private final LocalDate birthDate;
    private AccountStatus accountStatus;
    private final Set<Role> roles;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User(AccountId id, OrganizationId organizationId, Name name, Email email, LocalDate birthDate, String passwordHash, AccountStatus status) {
        this.id = id;
        this.organizationId = organizationId;
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;
        this.passwordHash = passwordHash;
        this.accountStatus = status;
        this.roles = new HashSet<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void setId(AccountId id) { this.id = id; }

    public int getAge() {
        return Period.between(this.birthDate, LocalDate.now()).getYears();
    }

    public void assignRole(Role role) {
        if (role.getScope() == RoleScope.SYSTEM && this.organizationId != null) {
            throw new IllegalStateException("Roles de sistema no pueden ser asignados a usuarios de una organización.");
        }
        this.roles.add(role);
        this.updatedAt = LocalDateTime.now();
    }

    public void changePassword(String newPasswordHash) {
        if (this.accountStatus != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Solo las cuentas activas pueden cambiar su contraseña.");
        }
        this.passwordHash = newPasswordHash;
        this.updatedAt = LocalDateTime.now();
    }

    public void activateAccount(int digitalConsentAge) {
        if (getAge() < digitalConsentAge) {
            throw new IllegalStateException("El usuario no cumple la edad de consentimiento digital.");
        }
        if (this.accountStatus == AccountStatus.TUTOR_MANAGED || this.accountStatus == AccountStatus.PENDING_VERIFICATION) {
            this.accountStatus = AccountStatus.ACTIVE;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void suspendAccount() {
        this.accountStatus = AccountStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
    }
}