package com.academia.domain.model.enums;

/**
 * Enumeration representing the status of a student's assignment submission.
 */
public enum SubmissionStatus {
    DRAFT("Work in progress, not yet submitted"),
    SUBMITTED("Submitted on time"),
    LATE("Submitted after the due date"),
    GRADED("Submission has been graded"),
    RETURNED("Graded submission returned to student"),
    RESUBMISSION_REQUIRED("Requires resubmission after feedback"),
    RESUBMITTED("Resubmitted after initial feedback");
    
    private final String description;
    
    SubmissionStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isCompleted() {
        return this != DRAFT;
    }
    
    public boolean canBeGraded() {
        return this == SUBMITTED || this == LATE || this == RESUBMITTED;
    }
    
    public boolean isGraded() {
        return this == GRADED || this == RETURNED;
    }
}