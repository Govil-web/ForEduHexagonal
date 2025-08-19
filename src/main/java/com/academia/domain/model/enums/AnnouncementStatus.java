package com.academia.domain.model.enums;

/**
 * Enumeration representing the status of announcements.
 */
public enum AnnouncementStatus {
    DRAFT("Draft - not yet published"),
    PUBLISHED("Published and visible to recipients"),
    ARCHIVED("Archived - no longer active"),
    EXPIRED("Expired - past expiration date");
    
    private final String description;
    
    AnnouncementStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isVisible() {
        return this == PUBLISHED;
    }
    
    public boolean canBeEdited() {
        return this == DRAFT;
    }
    
    public boolean isActive() {
        return this == PUBLISHED;
    }
}