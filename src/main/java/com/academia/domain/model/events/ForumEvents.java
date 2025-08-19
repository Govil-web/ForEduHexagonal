package com.academia.domain.model.events;

import com.academia.domain.model.valueobjects.ids.ForumThreadId;
import com.academia.domain.model.valueobjects.ids.ForumPostId;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.CourseId;

import java.time.Instant;


/**
 * Domain events related to Forum aggregate lifecycle and business operations.
 */
public class ForumEvents {
    
    public record ForumThreadCreatedEvent(
        Long eventId,
        Instant occurredOn,
        ForumThreadId threadId,
        CourseId courseId,
        AccountId authorId,
        String title
    ) implements DomainEvent {
        public ForumThreadCreatedEvent(ForumThreadId threadId, CourseId courseId, AccountId authorId, String title) {
            this(null, Instant.now(), threadId, courseId, authorId, title);
        }

        @Override
        public Long getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record ForumPostCreatedEvent(
        Long eventId,
        Instant occurredOn,
        ForumPostId postId,
        ForumThreadId threadId,
        AccountId authorId,
        boolean isReply
    ) implements DomainEvent {
        public ForumPostCreatedEvent(ForumPostId postId, ForumThreadId threadId, AccountId authorId, boolean isReply) {
            this(null, Instant.now(), postId, threadId, authorId, isReply);
        }

        @Override
        public Long getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record ForumThreadClosedEvent(
        Long eventId,
        Instant occurredOn,
        ForumThreadId threadId,
        AccountId closedBy
    ) implements DomainEvent {
        public ForumThreadClosedEvent(ForumThreadId threadId, AccountId closedBy) {
            this(null, Instant.now(), threadId, closedBy);
        }

        @Override
        public Long getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record ForumThreadLockedEvent(
        Long eventId,
        Instant occurredOn,
        ForumThreadId threadId,
        AccountId lockedBy,
        String reason
    ) implements DomainEvent {
        public ForumThreadLockedEvent(ForumThreadId threadId, AccountId lockedBy, String reason) {
            this(null, Instant.now(), threadId, lockedBy, reason);
        }

        @Override
        public Long getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record ForumThreadPinnedEvent(
        Long eventId,
        Instant occurredOn,
        ForumThreadId threadId,
        AccountId pinnedBy
    ) implements DomainEvent {
        public ForumThreadPinnedEvent(ForumThreadId threadId, AccountId pinnedBy) {
            this(null, Instant.now(), threadId, pinnedBy);
        }

        @Override
        public Long getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record ForumPostEditedEvent(
        Long eventId,
        Instant occurredOn,
        ForumPostId postId,
        ForumThreadId threadId,
        AccountId editedBy
    ) implements DomainEvent {
        public ForumPostEditedEvent(ForumPostId postId, ForumThreadId threadId, AccountId editedBy) {
            this(null, Instant.now(), postId, threadId, editedBy);
        }

        @Override
        public Long getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record ForumPostDeletedEvent(
        Long eventId,
        Instant occurredOn,
        ForumPostId postId,
        ForumThreadId threadId,
        AccountId deletedBy
    ) implements DomainEvent {
        public ForumPostDeletedEvent(ForumPostId postId, ForumThreadId threadId, AccountId deletedBy) {
            this(null, Instant.now(), postId, threadId, deletedBy);
        }

        @Override
        public Long getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record ForumThreadUnpinnedEvent(
        Long eventId,
        Instant occurredOn,
        ForumThreadId threadId,
        AccountId unpinnedBy
    ) implements DomainEvent {
        public ForumThreadUnpinnedEvent(ForumThreadId threadId, AccountId unpinnedBy) {
            this(null, Instant.now(), threadId, unpinnedBy);
        }

        @Override
        public Long getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record ForumThreadArchivedEvent(
        Long eventId,
        Instant occurredOn,
        ForumThreadId threadId,
        AccountId archivedBy
    ) implements DomainEvent {
        public ForumThreadArchivedEvent(ForumThreadId threadId, AccountId archivedBy) {
            this(null, Instant.now(), threadId, archivedBy);
        }

        @Override
        public Long getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
}