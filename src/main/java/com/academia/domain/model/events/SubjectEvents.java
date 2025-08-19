package com.academia.domain.model.events;

import com.academia.domain.model.valueobjects.ids.SubjectId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.academic.SubjectCode;

import java.time.Instant;

/**
 * Domain events related to Subject aggregate lifecycle and business operations.
 */
public class SubjectEvents {
    
    /**
     * Event published when a new subject is created in the organization.
     */
    public record SubjectCreatedEvent(
        Long eventId,
        Instant occurredOn,
        SubjectId subjectId,
        OrganizationId organizationId,
        SubjectCode subjectCode,
        String name
    ) implements DomainEvent {
        public SubjectCreatedEvent(SubjectId subjectId, OrganizationId organizationId, SubjectCode subjectCode, String name) {
            this(null, Instant.now(), subjectId, organizationId, subjectCode, name);
        }

        @Override
        public Long getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    /**
     * Event published when a subject is updated.
     */
    public record SubjectUpdatedEvent(
        Long eventId,
        Instant occurredOn,
        SubjectId subjectId,
        String previousName,
        String newName
    ) implements DomainEvent {
        public SubjectUpdatedEvent(SubjectId subjectId, String previousName, String newName) {
            this(null, Instant.now(), subjectId, previousName, newName);
        }

        @Override
        public Long getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    /**
     * Event published when a subject is activated.
     */
    public record SubjectActivatedEvent(
        Long eventId,
        Instant occurredOn,
        SubjectId subjectId,
        OrganizationId organizationId
    ) implements DomainEvent {
        public SubjectActivatedEvent(SubjectId subjectId, OrganizationId organizationId) {
            this(null, Instant.now(), subjectId, organizationId);
        }

        @Override
        public Long getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    /**
     * Event published when a subject is deactivated.
     */
    public record SubjectDeactivatedEvent(
        Long eventId,
        Instant occurredOn,
        SubjectId subjectId,
        OrganizationId organizationId
    ) implements DomainEvent {
        public SubjectDeactivatedEvent(SubjectId subjectId, OrganizationId organizationId) {
            this(null, Instant.now(), subjectId, organizationId);
        }

        @Override
        public Long getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    /**
     * Event published when prerequisites are added to a subject.
     */
    public record PrerequisitesAddedEvent(
        Long eventId,
        Instant occurredOn,
        SubjectId subjectId,
        SubjectId prerequisiteSubjectId
    ) implements DomainEvent {
        public PrerequisitesAddedEvent(SubjectId subjectId, SubjectId prerequisiteSubjectId) {
            this(null, Instant.now(), subjectId, prerequisiteSubjectId);
        }

        @Override
        public Long getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    /**
     * Event published when prerequisites are removed from a subject.
     */
    public record PrerequisitesRemovedEvent(
        Long eventId,
        Instant occurredOn,
        SubjectId subjectId,
        SubjectId prerequisiteSubjectId
    ) implements DomainEvent {
        public PrerequisitesRemovedEvent(SubjectId subjectId, SubjectId prerequisiteSubjectId) {
            this(null, Instant.now(), subjectId, prerequisiteSubjectId);
        }

        @Override
        public Long getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
}