package com.academia.domain.model.events;

import com.academia.domain.model.valueobjects.ids.SubmissionId;
import com.academia.domain.model.valueobjects.ids.AssignmentId;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.academic.Grade;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain events related to Submission aggregate lifecycle and business operations.
 */
public class SubmissionEvents {
    
    public record SubmissionCreatedEvent(
        UUID eventId,
        Instant occurredOn,
        SubmissionId submissionId,
        AssignmentId assignmentId,
        AccountId studentId
    ) implements DomainEvent {
        public SubmissionCreatedEvent(SubmissionId submissionId, AssignmentId assignmentId, AccountId studentId) {
            this(UUID.randomUUID(), Instant.now(), submissionId, assignmentId, studentId);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record SubmissionSubmittedEvent(
        UUID eventId,
        Instant occurredOn,
        SubmissionId submissionId,
        AssignmentId assignmentId,
        AccountId studentId,
        boolean isLate
    ) implements DomainEvent {
        public SubmissionSubmittedEvent(SubmissionId submissionId, AssignmentId assignmentId, AccountId studentId, boolean isLate) {
            this(UUID.randomUUID(), Instant.now(), submissionId, assignmentId, studentId, isLate);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record SubmissionGradedEvent(
        UUID eventId,
        Instant occurredOn,
        SubmissionId submissionId,
        AssignmentId assignmentId,
        AccountId studentId,
        AccountId graderId,
        Grade grade
    ) implements DomainEvent {
        public SubmissionGradedEvent(SubmissionId submissionId, AssignmentId assignmentId, AccountId studentId, AccountId graderId, Grade grade) {
            this(UUID.randomUUID(), Instant.now(), submissionId, assignmentId, studentId, graderId, grade);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record SubmissionReturnedEvent(
        UUID eventId,
        Instant occurredOn,
        SubmissionId submissionId,
        AssignmentId assignmentId,
        AccountId studentId
    ) implements DomainEvent {
        public SubmissionReturnedEvent(SubmissionId submissionId, AssignmentId assignmentId, AccountId studentId) {
            this(UUID.randomUUID(), Instant.now(), submissionId, assignmentId, studentId);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record ResubmissionRequiredEvent(
        UUID eventId,
        Instant occurredOn,
        SubmissionId submissionId,
        AssignmentId assignmentId,
        AccountId studentId,
        String feedback
    ) implements DomainEvent {
        public ResubmissionRequiredEvent(SubmissionId submissionId, AssignmentId assignmentId, AccountId studentId, String feedback) {
            this(UUID.randomUUID(), Instant.now(), submissionId, assignmentId, studentId, feedback);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record SubmissionResubmittedEvent(
        UUID eventId,
        Instant occurredOn,
        SubmissionId submissionId,
        AssignmentId assignmentId,
        AccountId studentId
    ) implements DomainEvent {
        public SubmissionResubmittedEvent(SubmissionId submissionId, AssignmentId assignmentId, AccountId studentId) {
            this(UUID.randomUUID(), Instant.now(), submissionId, assignmentId, studentId);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
}