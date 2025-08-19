package com.academia.domain.model.aggregates;

import com.academia.domain.model.events.DomainEvent;
import com.academia.domain.model.events.AssignmentEvents;
import com.academia.domain.model.valueobjects.ids.AssignmentId;
import com.academia.domain.model.valueobjects.ids.CourseId;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.academic.AssignmentTitle;
import com.academia.domain.model.valueobjects.academic.DueDate;
import com.academia.domain.model.enums.AssignmentType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Assignment aggregate root representing an academic assignment within a course.
 * Manages assignment lifecycle, due dates, and publication status.
 */
@Getter
public class Assignment {
    private final AssignmentId id;
    private final CourseId courseId;
    private final AccountId teacherId;
    private AssignmentTitle title;
    private String description;
    private final AssignmentType type;
    private DueDate dueDate;
    private boolean isPublished;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    
    public Assignment(AssignmentId id, CourseId courseId, AccountId teacherId, 
                     AssignmentTitle title, String description, AssignmentType type, DueDate dueDate) {
        this.id = id;
        this.courseId = courseId;
        this.teacherId = teacherId;
        this.title = title;
        this.description = description != null ? description.trim() : "";
        this.type = type;
        this.dueDate = dueDate;
        this.isPublished = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new AssignmentEvents.AssignmentCreatedEvent(
            id, courseId, teacherId, title.getValue(), type, dueDate));
    }
    
    /**
     * Updates the assignment title and description.
     */
    public void updateDetails(AssignmentTitle newTitle, String newDescription) {
        String previousTitle = this.title.getValue();
        this.title = newTitle;
        this.description = newDescription != null ? newDescription.trim() : "";
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new AssignmentEvents.AssignmentUpdatedEvent(id, previousTitle, newTitle.getValue()));
    }
    
    /**
     * Extends the due date for the assignment.
     * Can only be done by the teacher and if the assignment hasn't been published.
     */
    public void extendDueDate(DueDate newDueDate) {
        if (isPublished && newDueDate.getValue().isBefore(this.dueDate.getValue())) {
            throw new IllegalStateException("Cannot move due date earlier for a published assignment");
        }
        
        DueDate previousDueDate = this.dueDate;
        this.dueDate = newDueDate;
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new AssignmentEvents.AssignmentDueDateExtendedEvent(id, previousDueDate, newDueDate));
    }
    
    /**
     * Publishes the assignment making it visible to students.
     */
    public void publish() {
        if (!isPublished) {
            this.isPublished = true;
            this.updatedAt = LocalDateTime.now();
            domainEvents.add(new AssignmentEvents.AssignmentPublishedEvent(id, courseId));
        }
    }
    
    /**
     * Unpublishes the assignment hiding it from students.
     * Can only be done if no submissions have been made.
     */
    public void unpublish() {
        if (isPublished) {
            this.isPublished = false;
            this.updatedAt = LocalDateTime.now();
            domainEvents.add(new AssignmentEvents.AssignmentUnpublishedEvent(id, courseId));
        }
    }
    
    /**
     * Checks if the assignment is overdue.
     */
    public boolean isOverdue() {
        return dueDate.isPast();
    }
    
    /**
     * Checks if submissions can be accepted for this assignment.
     */
    public boolean canAcceptSubmissions() {
        return isPublished;
    }
    
    /**
     * Checks if the assignment is due within the specified hours.
     */
    public boolean isDueWithinHours(int hours) {
        return dueDate.isWithinHours(hours);
    }
    
    /**
     * Marks the assignment as having reached its due date.
     * This is typically called by a scheduled process.
     */
    public void markDueDateReached() {
        if (dueDate.isPast()) {
            domainEvents.add(new AssignmentEvents.AssignmentDueDateReachedEvent(id, courseId));
        }
    }
    
    public List<DomainEvent> getDomainEvents() {
        return List.copyOf(domainEvents);
    }
    
    public void clearDomainEvents() {
        domainEvents.clear();
    }
}