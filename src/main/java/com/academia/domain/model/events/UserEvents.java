package com.academia.domain.model.events;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.user.Email;
import java.time.Instant;


public class UserEvents {
    public record UserRegisteredEvent(Long eventId, Instant occurredOn, AccountId accountId, Email email) implements DomainEvent {
        public UserRegisteredEvent(AccountId accountId, Email email) {
            this(null, Instant.now(), accountId, email);
        }

        @Override
        public Long getEventId() {
           return eventId();
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn();
        }
    }
    public record AccountActivated(Long eventId, Instant occurredOn, AccountId accountId) implements DomainEvent {
        public AccountActivated(AccountId accountId) {
            this(null, Instant.now(), accountId);
        }

        @Override
        public Long getEventId() {
            return eventId();
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn();
        }
    }
}
