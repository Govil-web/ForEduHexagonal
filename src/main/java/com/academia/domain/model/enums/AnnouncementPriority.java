package com.academia.domain.model.enums;

/**
 * Enumeration representing the priority level of announcements.
 */
public enum AnnouncementPriority {
    LOW("Low priority - general information"),
    NORMAL("Normal priority - standard announcement"),
    HIGH("High priority - important information"),
    URGENT("Urgent - requires immediate attention"),
    CRITICAL("Critical - emergency announcement");
    
    private final String description;
    
    AnnouncementPriority(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean requiresImmediateAttention() {
        return this == URGENT || this == CRITICAL;
    }
    
    public boolean isHighPriority() {
        return this == HIGH || this == URGENT || this == CRITICAL;
    }
    
    public int getPriorityLevel() {
        return ordinal();
    }
}