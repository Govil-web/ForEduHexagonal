package com.academia.domain.model.aggregates;

import com.academia.domain.model.events.DomainEvent;
import com.academia.domain.model.events.AnnouncementEvents;
import com.academia.domain.model.valueobjects.ids.AnnouncementId;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.ids.CourseId;
import com.academia.domain.model.enums.AnnouncementPriority;
import com.academia.domain.model.enums.AnnouncementStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Announcement aggregate root representing a communication message within an organization or course.
 * Manages announcement lifecycle, visibility, and distribution.
 */
@Getter
public class Announcement {
    private final AnnouncementId id;
    private final OrganizationId organizationId;
    private final CourseId courseId; // null for organization-wide announcements
    private final AccountId authorId;
    private String title;
    private String content;
    private AnnouncementPriority priority;
    private AnnouncementStatus status;
    private LocalDateTime expirationDate;
    private final LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    
    public Announcement(AnnouncementId id, OrganizationId organizationId, CourseId courseId,
                       AccountId authorId, String title, String content, AnnouncementPriority priority,
                       LocalDateTime expirationDate) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Announcement title cannot be null or empty");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Announcement content cannot be null or empty");
        }
        if (expirationDate != null && expirationDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Expiration date cannot be in the past");
        }
        
        this.id = id;
        this.organizationId = organizationId;
        this.courseId = courseId;
        this.authorId = authorId;
        this.title = title.trim();
        this.content = content.trim();
        this.priority = priority != null ? priority : AnnouncementPriority.NORMAL;
        this.status = AnnouncementStatus.DRAFT;
        this.expirationDate = expirationDate;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new AnnouncementEvents.AnnouncementCreatedEvent(
            id, organizationId, authorId, title, this.priority));
    }
    
    /**
     * Updates the announcement title and content.
     */
    public void updateContent(String newTitle, String newContent, AccountId updatedBy) {
        if (!status.canBeEdited()) {
            throw new IllegalStateException("Cannot edit announcement in status: " + status);
        }
        
        if (newTitle == null || newTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Announcement title cannot be null or empty");
        }
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("Announcement content cannot be null or empty");
        }
        
        String previousTitle = this.title;
        this.title = newTitle.trim();
        this.content = newContent.trim();
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new AnnouncementEvents.AnnouncementUpdatedEvent(
            id, updatedBy, previousTitle, newTitle));
    }
    
    /**
     * Updates the announcement priority.
     */
    public void updatePriority(AnnouncementPriority newPriority) {
        if (!status.canBeEdited()) {
            throw new IllegalStateException("Cannot edit announcement in status: " + status);
        }
        
        this.priority = newPriority != null ? newPriority : AnnouncementPriority.NORMAL;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Updates the expiration date.
     */
    public void updateExpirationDate(LocalDateTime newExpirationDate) {
        if (!status.canBeEdited()) {
            throw new IllegalStateException("Cannot edit announcement in status: " + status);
        }
        
        if (newExpirationDate != null && newExpirationDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Expiration date cannot be in the past");
        }
        
        this.expirationDate = newExpirationDate;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Publishes the announcement making it visible to the target audience.
     */
    public void publish() {
        if (status != AnnouncementStatus.DRAFT) {
            throw new IllegalStateException("Can only publish draft announcements");
        }
        
        if (isExpired()) {
            throw new IllegalStateException("Cannot publish an expired announcement");
        }
        
        this.status = AnnouncementStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new AnnouncementEvents.AnnouncementPublishedEvent(
            id, organizationId, courseId, authorId, title, priority, publishedAt));
        
        // Generate high-priority notification event if applicable
        if (priority.isHighPriority()) {
            domainEvents.add(new AnnouncementEvents.HighPriorityAnnouncementEvent(
                id, organizationId, courseId, title, priority));
        }
    }
    
    /**
     * Archives the announcement removing it from active view.
     */
    public void archive(AccountId archivedBy) {
        if (status == AnnouncementStatus.ARCHIVED) {
            return; // Already archived
        }
        
        this.status = AnnouncementStatus.ARCHIVED;
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new AnnouncementEvents.AnnouncementArchivedEvent(id, archivedBy));
    }
    
    /**
     * Marks the announcement as expired when the expiration date is reached.
     */
    public void markAsExpired() {
        if (status == AnnouncementStatus.PUBLISHED && isExpired()) {
            this.status = AnnouncementStatus.EXPIRED;
            this.updatedAt = LocalDateTime.now();
            
            domainEvents.add(new AnnouncementEvents.AnnouncementExpiredEvent(id, organizationId));
        }
    }
    
    /**
     * Checks if the announcement is expired.
     */
    public boolean isExpired() {
        return expirationDate != null && LocalDateTime.now().isAfter(expirationDate);
    }
    
    /**
     * Checks if the announcement is visible to users.
     */
    public boolean isVisible() {
        return status.isVisible() && !isExpired();
    }
    
    /**
     * Checks if the announcement is organization-wide (not course-specific).
     */
    public boolean isOrganizationWide() {
        return courseId == null;
    }
    
    /**
     * Checks if the announcement is course-specific.
     */
    public boolean isCourseSpecific() {
        return courseId != null;
    }
    
    /**
     * Checks if the announcement requires immediate attention.
     */
    public boolean requiresImmediateAttention() {
        return priority.requiresImmediateAttention();
    }
    
    /**
     * Gets the days until expiration. Returns null if no expiration date.
     */
    public Long getDaysUntilExpiration() {
        if (expirationDate == null) {
            return null;
        }
        
        long days = java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now(), expirationDate);
        return Math.max(0, days);
    }
    
    public List<DomainEvent> getDomainEvents() {
        return List.copyOf(domainEvents);
    }
    
    public void clearDomainEvents() {
        domainEvents.clear();
    }
}