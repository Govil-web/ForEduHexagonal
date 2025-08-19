package com.academia.domain.model.events;

import com.academia.domain.model.valueobjects.ids.SubjectId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.academic.SubjectCode;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain events related to Subject aggregate lifecycle and business operations.
 */
public class SubjectEvents {
    
    /**
     * Event published when a new subject is created in the organization.
     */
    public record SubjectCreatedEvent(
        UUID eventId,
        Instant occurredOn,
        SubjectId subjectId,
        OrganizationId organizationId,
        SubjectCode subjectCode,
        String name
    ) implements DomainEvent {
        public SubjectCreatedEvent(SubjectId subjectId, OrganizationId organizationId, SubjectCode subjectCode, String name) {
            this(UUID.randomUUID(), Instant.now(), subjectId, organizationId, subjectCode, name);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    /**
     * Event published when a subject is updated.
     */
    public record SubjectUpdatedEvent(
        UUID eventId,
        Instant occurredOn,
        SubjectId subjectId,
        String previousName,
        String newName
    ) implements DomainEvent {
        public SubjectUpdatedEvent(SubjectId subjectId, String previousName, String newName) {
            this(UUID.randomUUID(), Instant.now(), subjectId, previousName, newName);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    /**
     * Event published when a subject is activated.
     */
    public record SubjectActivatedEvent(
        UUID eventId,
        Instant occurredOn,
        SubjectId subjectId,
        OrganizationId organizationId
    ) implements DomainEvent {
        public SubjectActivatedEvent(SubjectId subjectId, OrganizationId organizationId) {
            this(UUID.randomUUID(), Instant.now(), subjectId, organizationId);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    /**
     * Event published when a subject is deactivated.
     */
    public record SubjectDeactivatedEvent(
        UUID eventId,
        Instant occurredOn,
        SubjectId subjectId,
        OrganizationId organizationId
    ) implements DomainEvent {
        public SubjectDeactivatedEvent(SubjectId subjectId, OrganizationId organizationId) {
            this(UUID.randomUUID(), Instant.now(), subjectId, organizationId);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    /**
     * Event published when prerequisites are added to a subject.
     */
    public record PrerequisitesAddedEvent(
        UUID eventId,
        Instant occurredOn,
        SubjectId subjectId,
        SubjectId prerequisiteSubjectId
    ) implements DomainEvent {
        public PrerequisitesAddedEvent(SubjectId subjectId, SubjectId prerequisiteSubjectId) {
            this(UUID.randomUUID(), Instant.now(), subjectId, prerequisiteSubjectId);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    /**
     * Event published when prerequisites are removed from a subject.
     */
    public record PrerequisitesRemovedEvent(
        UUID eventId,
        Instant occurredOn,
        SubjectId subjectId,
        SubjectId prerequisiteSubjectId
    ) implements DomainEvent {
        public PrerequisitesRemovedEvent(SubjectId subjectId, SubjectId prerequisiteSubjectId) {
            this(UUID.randomUUID(), Instant.now(), subjectId, prerequisiteSubjectId);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
}