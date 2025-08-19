package com.academia.domain.model.events;

import com.academia.domain.model.valueobjects.ids.ForumThreadId;
import com.academia.domain.model.valueobjects.ids.ForumPostId;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.CourseId;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain events related to Forum aggregate lifecycle and business operations.
 */
public class ForumEvents {
    
    public record ForumThreadCreatedEvent(
        UUID eventId,
        Instant occurredOn,
        ForumThreadId threadId,
        CourseId courseId,
        AccountId authorId,
        String title
    ) implements DomainEvent {
        public ForumThreadCreatedEvent(ForumThreadId threadId, CourseId courseId, AccountId authorId, String title) {
            this(UUID.randomUUID(), Instant.now(), threadId, courseId, authorId, title);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record ForumPostCreatedEvent(
        UUID eventId,
        Instant occurredOn,
        ForumPostId postId,
        ForumThreadId threadId,
        AccountId authorId,
        boolean isReply
    ) implements DomainEvent {
        public ForumPostCreatedEvent(ForumPostId postId, ForumThreadId threadId, AccountId authorId, boolean isReply) {
            this(UUID.randomUUID(), Instant.now(), postId, threadId, authorId, isReply);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record ForumThreadClosedEvent(
        UUID eventId,
        Instant occurredOn,
        ForumThreadId threadId,
        AccountId closedBy
    ) implements DomainEvent {
        public ForumThreadClosedEvent(ForumThreadId threadId, AccountId closedBy) {
            this(UUID.randomUUID(), Instant.now(), threadId, closedBy);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record ForumThreadLockedEvent(
        UUID eventId,
        Instant occurredOn,
        ForumThreadId threadId,
        AccountId lockedBy,
        String reason
    ) implements DomainEvent {
        public ForumThreadLockedEvent(ForumThreadId threadId, AccountId lockedBy, String reason) {
            this(UUID.randomUUID(), Instant.now(), threadId, lockedBy, reason);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record ForumThreadPinnedEvent(
        UUID eventId,
        Instant occurredOn,
        ForumThreadId threadId,
        AccountId pinnedBy
    ) implements DomainEvent {
        public ForumThreadPinnedEvent(ForumThreadId threadId, AccountId pinnedBy) {
            this(UUID.randomUUID(), Instant.now(), threadId, pinnedBy);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record ForumPostEditedEvent(
        UUID eventId,
        Instant occurredOn,
        ForumPostId postId,
        ForumThreadId threadId,
        AccountId editedBy
    ) implements DomainEvent {
        public ForumPostEditedEvent(ForumPostId postId, ForumThreadId threadId, AccountId editedBy) {
            this(UUID.randomUUID(), Instant.now(), postId, threadId, editedBy);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record ForumPostDeletedEvent(
        UUID eventId,
        Instant occurredOn,
        ForumPostId postId,
        ForumThreadId threadId,
        AccountId deletedBy
    ) implements DomainEvent {
        public ForumPostDeletedEvent(ForumPostId postId, ForumThreadId threadId, AccountId deletedBy) {
            this(UUID.randomUUID(), Instant.now(), postId, threadId, deletedBy);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record ForumThreadUnpinnedEvent(
        UUID eventId,
        Instant occurredOn,
        ForumThreadId threadId,
        AccountId unpinnedBy
    ) implements DomainEvent {
        public ForumThreadUnpinnedEvent(ForumThreadId threadId, AccountId unpinnedBy) {
            this(UUID.randomUUID(), Instant.now(), threadId, unpinnedBy);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record ForumThreadArchivedEvent(
        UUID eventId,
        Instant occurredOn,
        ForumThreadId threadId,
        AccountId archivedBy
    ) implements DomainEvent {
        public ForumThreadArchivedEvent(ForumThreadId threadId, AccountId archivedBy) {
            this(UUID.randomUUID(), Instant.now(), threadId, archivedBy);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
}