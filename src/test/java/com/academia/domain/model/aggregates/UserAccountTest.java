package com.academia.domain.model.aggregates;

import com.academia.domain.model.entities.User;
import com.academia.domain.model.enums.AccountStatus;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.user.Email;
import com.academia.domain.model.valueobjects.user.Name;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserAccountTest {

    private Organization organization;
    private final OrganizationId orgId = new OrganizationId(1L);

    @BeforeEach
    void setUp() {
        organization = new Organization(orgId, "Universidad del Futuro", 16);
    }

    @Test
    @DisplayName("Debe registrar un usuario menor de edad con estado TUTOR_MANAGED")
    void register_shouldSetTutorManaged_whenUserIsUnderage() {
        // Arrange
        // CORRECCIÓN: Se pasan todos los argumentos requeridos por el constructor de User.
        User underageUser = new User(
                new AccountId(1L),
                orgId,
                new Name("Luis", "Vega"),
                new Email("luis.vega@unifuturo.edu"),
                LocalDate.now().minusYears(15), // 15 años
                null,
                AccountStatus.TUTOR_MANAGED
        );

        // Act
        UserAccount userAccount = UserAccount.register(underageUser, organization);

        // Assert
        assertEquals(AccountStatus.TUTOR_MANAGED, userAccount.getUser().getAccountStatus());
        assertFalse(userAccount.getDomainEvents().isEmpty());
    }

    @Test
    @DisplayName("Debe activar la cuenta si el usuario cumple la edad de consentimiento")
    void activate_shouldSetStatusToActive_whenUserMeetsConsentAge() {
        // Arrange
        // CORRECCIÓN: Se pasan todos los argumentos requeridos por el constructor de User.
        User userToActivate = new User(
                new AccountId(2L),
                orgId,
                new Name("Sofia", "Castillo"),
                new Email("sofia.castillo@unifuturo.edu"),
                LocalDate.now().minusYears(17), // 17 años > 16
                null,
                AccountStatus.TUTOR_MANAGED
        );
        UserAccount userAccount = new UserAccount(userToActivate);

        // Act
        userAccount.activate(organization);

        // Assert
        assertEquals(AccountStatus.ACTIVE, userAccount.getUser().getAccountStatus());
        assertEquals(1, userAccount.getDomainEvents().size());
    }

    @Test
    @DisplayName("Debe lanzar una excepción al activar un usuario que no cumple la edad")
    void activate_shouldThrowException_whenUserIsUnderConsentAge() {
        // Arrange
        // CORRECCIÓN: Se pasan todos los argumentos requeridos por el constructor de User.
        User underageUser = new User(
                new AccountId(3L),
                orgId,
                new Name("Carlos", "Ruiz"),
                new Email("carlos.ruiz@pequenomundo.edu"),
                LocalDate.now().minusYears(15), // 15 años < 16
                null,
                AccountStatus.TUTOR_MANAGED
        );
        UserAccount userAccount = new UserAccount(underageUser);

        // Act & Assert
        // CORRECCIÓN: Se simplifica la lambda como sugiere el IDE.
        assertThrows(IllegalStateException.class, () -> userAccount.activate(organization));
    }
}