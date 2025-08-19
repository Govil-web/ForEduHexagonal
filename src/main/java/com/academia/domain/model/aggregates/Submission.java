package com.academia.domain.model.aggregates;

import com.academia.domain.model.events.DomainEvent;
import com.academia.domain.model.events.SubmissionEvents;
import com.academia.domain.model.valueobjects.ids.SubmissionId;
import com.academia.domain.model.valueobjects.ids.AssignmentId;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.academic.Grade;
import com.academia.domain.model.enums.SubmissionStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Submission aggregate root representing a student's submission for an assignment.
 * Manages submission lifecycle, grading, and feedback.
 */
@Getter
public class Submission {
    private final SubmissionId id;
    private final AssignmentId assignmentId;
    private final AccountId studentId;
    private String content;
    private SubmissionStatus status;
    private Grade grade;
    private String feedback;
    private AccountId graderId;
    private final LocalDateTime createdAt;
    private LocalDateTime submittedAt;
    private LocalDateTime gradedAt;
    private LocalDateTime updatedAt;
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    
    public Submission(SubmissionId id, AssignmentId assignmentId, AccountId studentId, String content) {
        this.id = id;
        this.assignmentId = assignmentId;
        this.studentId = studentId;
        this.content = content != null ? content.trim() : "";
        this.status = SubmissionStatus.DRAFT;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new SubmissionEvents.SubmissionCreatedEvent(id, assignmentId, studentId));
    }
    
    /**
     * Updates the submission content while it's still in draft status.
     */
    public void updateContent(String newContent) {
        if (status != SubmissionStatus.DRAFT) {
            throw new IllegalStateException("Cannot update content of a submitted assignment");
        }
        
        this.content = newContent != null ? newContent.trim() : "";
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Submits the assignment to the teacher for grading.
     */
    public void submit(LocalDateTime assignmentDueDate) {
        if (status != SubmissionStatus.DRAFT) {
            throw new IllegalStateException("Can only submit assignments in DRAFT status");
        }
        
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalStateException("Cannot submit an assignment without content");
        }
        
        this.submittedAt = LocalDateTime.now();
        boolean isLate = submittedAt.isAfter(assignmentDueDate);
        this.status = isLate ? SubmissionStatus.LATE : SubmissionStatus.SUBMITTED;
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new SubmissionEvents.SubmissionSubmittedEvent(id, assignmentId, studentId, isLate));
    }
    
    /**
     * Grades the submission by a teacher.
     */
    public void grade(Grade grade, String feedback, AccountId graderId) {
        if (!status.canBeGraded()) {
            throw new IllegalStateException("Cannot grade submission in status: " + status);
        }
        
        this.grade = grade;
        this.feedback = feedback != null ? feedback.trim() : "";
        this.graderId = graderId;
        this.status = SubmissionStatus.GRADED;
        this.gradedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new SubmissionEvents.SubmissionGradedEvent(id, assignmentId, studentId, graderId, grade));
    }
    
    /**
     * Returns the graded submission to the student.
     */
    public void returnToStudent() {
        if (status != SubmissionStatus.GRADED) {
            throw new IllegalStateException("Can only return graded submissions");
        }
        
        this.status = SubmissionStatus.RETURNED;
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new SubmissionEvents.SubmissionReturnedEvent(id, assignmentId, studentId));
    }
    
    /**
     * Requires the student to resubmit the assignment.
     */
    public void requireResubmission(String feedback, AccountId graderId) {
        if (!status.isGraded()) {
            throw new IllegalStateException("Can only require resubmission for graded submissions");
        }
        
        this.feedback = feedback != null ? feedback.trim() : "";
        this.graderId = graderId;
        this.status = SubmissionStatus.RESUBMISSION_REQUIRED;
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new SubmissionEvents.ResubmissionRequiredEvent(id, assignmentId, studentId, feedback));
    }
    
    /**
     * Resubmits the assignment after feedback.
     */
    public void resubmit(String newContent) {
        if (status != SubmissionStatus.RESUBMISSION_REQUIRED) {
            throw new IllegalStateException("Can only resubmit when resubmission is required");
        }
        
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalStateException("Cannot resubmit an assignment without content");
        }
        
        this.content = newContent.trim();
        this.status = SubmissionStatus.RESUBMITTED;
        this.submittedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new SubmissionEvents.SubmissionResubmittedEvent(id, assignmentId, studentId));
    }
    
    /**
     * Checks if the submission is late.
     */
    public boolean isLate() {
        return status == SubmissionStatus.LATE;
    }
    
    /**
     * Checks if the submission has been completed (submitted).
     */
    public boolean isCompleted() {
        return status.isCompleted();
    }
    
    /**
     * Checks if the submission has been graded.
     */
    public boolean isGraded() {
        return status.isGraded();
    }
    
    /**
     * Checks if the submission can be edited.
     */
    public boolean canBeEdited() {
        return status == SubmissionStatus.DRAFT || status == SubmissionStatus.RESUBMISSION_REQUIRED;
    }
    
    public List<DomainEvent> getDomainEvents() {
        return List.copyOf(domainEvents);
    }
    
    public void clearDomainEvents() {
        domainEvents.clear();
    }
}