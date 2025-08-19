package com.academia.domain.model.events;

import com.academia.domain.model.valueobjects.ids.AssignmentId;
import com.academia.domain.model.valueobjects.ids.CourseId;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.academic.DueDate;
import com.academia.domain.model.enums.AssignmentType;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain events related to Assignment aggregate lifecycle and business operations.
 */
public class AssignmentEvents {
    
    /**
     * Event published when a new assignment is created.
     */
    public record AssignmentCreatedEvent(
        UUID eventId,
        Instant occurredOn,
        AssignmentId assignmentId,
        CourseId courseId,
        AccountId teacherId,
        String title,
        AssignmentType type,
        DueDate dueDate
    ) implements DomainEvent {
        public AssignmentCreatedEvent(AssignmentId assignmentId, CourseId courseId, AccountId teacherId, String title, AssignmentType type, DueDate dueDate) {
            this(UUID.randomUUID(), Instant.now(), assignmentId, courseId, teacherId, title, type, dueDate);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    /**
     * Event published when an assignment is updated.
     */
    public record AssignmentUpdatedEvent(
        UUID eventId,
        Instant occurredOn,
        AssignmentId assignmentId,
        String previousTitle,
        String newTitle
    ) implements DomainEvent {
        public AssignmentUpdatedEvent(AssignmentId assignmentId, String previousTitle, String newTitle) {
            this(UUID.randomUUID(), Instant.now(), assignmentId, previousTitle, newTitle);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    /**
     * Event published when an assignment due date is extended.
     */
    public record AssignmentDueDateExtendedEvent(
        UUID eventId,
        Instant occurredOn,
        AssignmentId assignmentId,
        DueDate previousDueDate,
        DueDate newDueDate
    ) implements DomainEvent {
        public AssignmentDueDateExtendedEvent(AssignmentId assignmentId, DueDate previousDueDate, DueDate newDueDate) {
            this(UUID.randomUUID(), Instant.now(), assignmentId, previousDueDate, newDueDate);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    /**
     * Event published when an assignment is published to students.
     */
    public record AssignmentPublishedEvent(
        UUID eventId,
        Instant occurredOn,
        AssignmentId assignmentId,
        CourseId courseId
    ) implements DomainEvent {
        public AssignmentPublishedEvent(AssignmentId assignmentId, CourseId courseId) {
            this(UUID.randomUUID(), Instant.now(), assignmentId, courseId);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    /**
     * Event published when an assignment is unpublished.
     */
    public record AssignmentUnpublishedEvent(
        UUID eventId,
        Instant occurredOn,
        AssignmentId assignmentId,
        CourseId courseId
    ) implements DomainEvent {
        public AssignmentUnpublishedEvent(AssignmentId assignmentId, CourseId courseId) {
            this(UUID.randomUUID(), Instant.now(), assignmentId, courseId);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    /**
     * Event published when an assignment due date is reached.
     */
    public record AssignmentDueDateReachedEvent(
        UUID eventId,
        Instant occurredOn,
        AssignmentId assignmentId,
        CourseId courseId
    ) implements DomainEvent {
        public AssignmentDueDateReachedEvent(AssignmentId assignmentId, CourseId courseId) {
            this(UUID.randomUUID(), Instant.now(), assignmentId, courseId);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
}