package com.academia.domain.model.events;

import com.academia.domain.model.valueobjects.ids.AnnouncementId;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.ids.CourseId;
import com.academia.domain.model.enums.AnnouncementPriority;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Domain events related to Announcement aggregate lifecycle and business operations.
 */
public class AnnouncementEvents {
    
    /**
     * Event published when a new announcement is created.
     */
    public record AnnouncementCreatedEvent(
        Long eventId,
        Instant occurredOn,
        AnnouncementId announcementId,
        OrganizationId organizationId,
        AccountId authorId,
        String title,
        AnnouncementPriority priority
    ) implements DomainEvent {
        public AnnouncementCreatedEvent(AnnouncementId announcementId, OrganizationId organizationId, AccountId authorId, String title, AnnouncementPriority priority) {
            this(null, Instant.now(), announcementId, organizationId, authorId, title, priority);
        }

        @Override
        public Long getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    /**
     * Event published when an announcement is published.
     */
    public record AnnouncementPublishedEvent(
        Long eventId,
        Instant occurredOn,
        AnnouncementId announcementId,
        OrganizationId organizationId,
        CourseId courseId, // null for organization-wide announcements
        AccountId authorId,
        String title,
        AnnouncementPriority priority,
        LocalDateTime publishedAt
    ) implements DomainEvent {
        public AnnouncementPublishedEvent(AnnouncementId announcementId, OrganizationId organizationId, CourseId courseId, AccountId authorId, String title, AnnouncementPriority priority, LocalDateTime publishedAt) {
            this(null, Instant.now(), announcementId, organizationId, courseId, authorId, title, priority, publishedAt);
        }

        @Override
        public Long getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    /**
     * Event published when an announcement is updated.
     */
    public record AnnouncementUpdatedEvent(
        Long eventId,
        Instant occurredOn,
        AnnouncementId announcementId,
        AccountId updatedBy,
        String previousTitle,
        String newTitle
    ) implements DomainEvent {
        public AnnouncementUpdatedEvent(AnnouncementId announcementId, AccountId updatedBy, String previousTitle, String newTitle) {
            this(null, Instant.now(), announcementId, updatedBy, previousTitle, newTitle);
        }

        @Override
        public Long getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    /**
     * Event published when an announcement is archived.
     */
    public record AnnouncementArchivedEvent(
        Long eventId,
        Instant occurredOn,
        AnnouncementId announcementId,
        AccountId archivedBy
    ) implements DomainEvent {
        public AnnouncementArchivedEvent(AnnouncementId announcementId, AccountId archivedBy) {
            this(null, Instant.now(), announcementId, archivedBy);
        }

        @Override
        public Long getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    /**
     * Event published when an announcement expires.
     */
    public record AnnouncementExpiredEvent(
        Long eventId,
        Instant occurredOn,
        AnnouncementId announcementId,
        OrganizationId organizationId
    ) implements DomainEvent {
        public AnnouncementExpiredEvent(AnnouncementId announcementId, OrganizationId organizationId) {
            this(null, Instant.now(), announcementId, organizationId);
        }

        @Override
        public Long getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    /**
     * Event published when a high-priority announcement is created.
     */
    public record HighPriorityAnnouncementEvent(
        Long eventId,
        Instant occurredOn,
        AnnouncementId announcementId,
        OrganizationId organizationId,
        CourseId courseId,
        String title,
        AnnouncementPriority priority
    ) implements DomainEvent {
        public HighPriorityAnnouncementEvent(AnnouncementId announcementId, OrganizationId organizationId, CourseId courseId, String title, AnnouncementPriority priority) {
            this(null, Instant.now(), announcementId, organizationId, courseId, title, priority);
        }

        @Override
        public Long getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
}