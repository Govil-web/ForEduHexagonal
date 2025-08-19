package com.academia.domain.model.events;

import com.academia.domain.model.valueobjects.ids.AcademicTermId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.academic.TermDates;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain events related to AcademicTerm aggregate lifecycle and business operations.
 */
public class AcademicTermEvents {
    
    public record AcademicTermCreatedEvent(
        UUID eventId,
        Instant occurredOn,
        AcademicTermId termId,
        OrganizationId organizationId,
        String termName,
        TermDates termDates
    ) implements DomainEvent {
        public AcademicTermCreatedEvent(AcademicTermId termId, OrganizationId organizationId, String termName, TermDates termDates) {
            this(UUID.randomUUID(), Instant.now(), termId, organizationId, termName, termDates);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record AcademicTermStartedEvent(
        UUID eventId,
        Instant occurredOn,
        AcademicTermId termId,
        OrganizationId organizationId
    ) implements DomainEvent {
        public AcademicTermStartedEvent(AcademicTermId termId, OrganizationId organizationId) {
            this(UUID.randomUUID(), Instant.now(), termId, organizationId);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record AcademicTermEndedEvent(
        UUID eventId,
        Instant occurredOn,
        AcademicTermId termId,
        OrganizationId organizationId
    ) implements DomainEvent {
        public AcademicTermEndedEvent(AcademicTermId termId, OrganizationId organizationId) {
            this(UUID.randomUUID(), Instant.now(), termId, organizationId);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record AcademicTermDatesUpdatedEvent(
        UUID eventId,
        Instant occurredOn,
        AcademicTermId termId,
        OrganizationId organizationId,
        TermDates previousDates,
        TermDates newDates
    ) implements DomainEvent {
        public AcademicTermDatesUpdatedEvent(AcademicTermId termId, OrganizationId organizationId, TermDates previousDates, TermDates newDates) {
            this(UUID.randomUUID(), Instant.now(), termId, organizationId, previousDates, newDates);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record AcademicTermActivatedEvent(
        UUID eventId,
        Instant occurredOn,
        AcademicTermId termId,
        OrganizationId organizationId
    ) implements DomainEvent {
        public AcademicTermActivatedEvent(AcademicTermId termId, OrganizationId organizationId) {
            this(UUID.randomUUID(), Instant.now(), termId, organizationId);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record AcademicTermDeactivatedEvent(
        UUID eventId,
        Instant occurredOn,
        AcademicTermId termId,
        OrganizationId organizationId
    ) implements DomainEvent {
        public AcademicTermDeactivatedEvent(AcademicTermId termId, OrganizationId organizationId) {
            this(UUID.randomUUID(), Instant.now(), termId, organizationId);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
}