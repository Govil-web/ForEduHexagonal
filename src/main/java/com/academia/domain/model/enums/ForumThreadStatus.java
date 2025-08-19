package com.academia.domain.model.enums;

/**
 * Enumeration representing the status of forum threads.
 */
public enum ForumThreadStatus {
    OPEN("Thread is open for discussion"),
    CLOSED("Thread is closed - no new posts allowed"),
    LOCKED("Thread is locked by moderator"),
    PINNED("Thread is pinned to the top"),
    ARCHIVED("Thread is archived");
    
    private final String description;
    
    ForumThreadStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean allowsNewPosts() {
        return this == OPEN || this == PINNED;
    }
    
    public boolean isActive() {
        return this != ARCHIVED;
    }
    
    public boolean isModeratorAction() {
        return this == LOCKED || this == PINNED;
    }
}