package com.academia.domain.model.events;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.user.Email;
import java.time.Instant;
import java.util.UUID;

public class UserAccountEvents {
    public record UserRegisteredEvent(UUID eventId, Instant occurredOn, AccountId accountId, Email email) implements DomainEvent {
        public UserRegisteredEvent(AccountId accountId, Email email) {
            this(UUID.randomUUID(), Instant.now(), accountId, email);
        }

        @Override
        public UUID getEventId() {
            return eventId();
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn();
        }
    }
    public record AccountActivated(UUID eventId, Instant occurredOn, AccountId accountId) implements DomainEvent {
        public AccountActivated(AccountId accountId) {
            this(UUID.randomUUID(), Instant.now(), accountId);
        }

        @Override
        public UUID getEventId() {
            return eventId();
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn();
        }
    }
}
