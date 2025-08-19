package com.academia.domain.model.enums;

/**
 * Enumeration representing different types of assignments in the academic system.
 */
public enum AssignmentType {
    HOMEWORK("Homework assignment to be completed individually"),
    QUIZ("Short assessment covering recent material"),
    EXAM("Comprehensive examination"),
    PROJECT("Extended project requiring research and development"),
    LAB("Laboratory exercise or practical work"),
    PRESENTATION("Oral presentation or demonstration"),
    ESSAY("Written essay assignment"),
    GROUP_WORK("Collaborative assignment for teams"),
    RESEARCH("Research assignment requiring investigation"),
    PRACTICAL("Hands-on practical exercise");
    
    private final String description;
    
    AssignmentType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isCollaborative() {
        return this == GROUP_WORK;
    }
    
    public boolean isExamination() {
        return this == QUIZ || this == EXAM;
    }
}