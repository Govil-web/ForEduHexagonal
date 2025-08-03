package com.academia.domain.model.aggregates;
import com.academia.domain.model.entities.Role;
import com.academia.domain.model.entities.User;
import com.academia.domain.model.enums.AccountStatus;
import com.academia.domain.model.events.DomainEvent;
import com.academia.domain.model.events.UserAccountEvents;
import com.academia.domain.model.valueobjects.user.HashedPassword;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class UserAccount {
    private final User user;
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    public UserAccount(User user) {
        this.user = user;
    }

    public static UserAccount register(User user, Organization organization) {
        // Lógica de negocio: determinar el estado inicial de la cuenta
        if (user.getAge() < organization.getDigitalConsentAge()) {
            if (user.getAccountStatus() != AccountStatus.TUTOR_MANAGED) {
                // Esta lógica es para asegurar que el estado sea el correcto.
                // En un caso de uso, el estado se setearía directamente en el constructor de User.
                // Aquí se demuestra una validación de invariante.
                throw new IllegalStateException("El estado inicial de un menor debe ser TUTOR_MANAGED");
            }
        }
        UserAccount userAccount = new UserAccount(user);
        userAccount.domainEvents.add(new UserAccountEvents.UserRegisteredEvent(user.getId(), user.getEmail()));
        return userAccount;
    }

    public void activate(Organization organization) {
        if (user.getAge() < organization.getDigitalConsentAge()) {
            throw new IllegalStateException("No se puede activar la cuenta: el usuario no cumple la edad de consentimiento digital (" + organization.getDigitalConsentAge() + " años).");
        }
        user.activateAccount(organization.getDigitalConsentAge());
        domainEvents.add(new UserAccountEvents.AccountActivated(user.getId()));
    }

    public void changePassword(HashedPassword newPassword, HashedPassword oldPassword) {
        // Lógica de negocio: verificar la contraseña anterior
        if (!this.user.getPasswordHash().equals(oldPassword.getValue())) {
            throw new IllegalArgumentException("La contraseña anterior no es correcta.");
        }
        user.changePassword(newPassword.getValue());
    }

    public List<DomainEvent> getDomainEvents() {
        return List.copyOf(domainEvents);
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }
}
