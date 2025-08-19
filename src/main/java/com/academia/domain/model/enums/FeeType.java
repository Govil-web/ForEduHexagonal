package com.academia.domain.model.enums;

/**
 * Enumeration representing different types of fees in the academic system.
 */
public enum FeeType {
    TUITION("Tuition fees for academic instruction"),
    REGISTRATION("Registration and enrollment fees"),
    LABORATORY("Laboratory usage and equipment fees"),
    LIBRARY("Library services and resources fees"),
    TECHNOLOGY("Technology and computer usage fees"),
    ACTIVITY("Student activities and sports fees"),
    PARKING("Vehicle parking fees"),
    HEALTH("Health services and insurance fees"),
    HOUSING("Dormitory and accommodation fees"),
    MEAL_PLAN("Cafeteria and meal plan fees"),
    GRADUATION("Graduation ceremony and diploma fees"),
    LATE_PAYMENT("Late payment penalty fees"),
    EXAM("Special examination fees"),
    TRANSCRIPT("Academic transcript fees"),
    APPLICATION("Application processing fees"),
    MATERIAL("Course materials and textbook fees"),
    TRANSPORTATION("School transportation fees"),
    MAINTENANCE("Facility maintenance fees"),
    OTHER("Other miscellaneous fees");
    
    private final String description;
    
    FeeType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isRecurring() {
        return this == TUITION || this == HOUSING || this == MEAL_PLAN || this == TRANSPORTATION;
    }
    
    public boolean isPenalty() {
        return this == LATE_PAYMENT;
    }
    
    public boolean isOneTime() {
        return this == REGISTRATION || this == GRADUATION || this == APPLICATION || this == TRANSCRIPT;
    }
}