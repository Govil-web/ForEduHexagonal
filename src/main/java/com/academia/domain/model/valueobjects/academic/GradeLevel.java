package com.academia.domain.model.valueobjects.academic;

import lombok.Value;

/**
 * Value object representing an academic grade level.
 * Supports various educational systems (elementary, middle, high school, university).
 */
@Value
public class GradeLevel {
    String value;
    
    public GradeLevel(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Grade level cannot be null or empty");
        }
        if (value.length() > 50) {
            throw new IllegalArgumentException("Grade level cannot exceed 50 characters");
        }
        this.value = value.trim();
    }
    
    public boolean isElementary() {
        return value.matches("^(K|[1-5])$|^Grade [1-5]$|^Elementary.*");
    }
    
    public boolean isMiddleSchool() {
        return value.matches("^[6-8]$|^Grade [6-8]$|^Middle.*");
    }
    
    public boolean isHighSchool() {
        return value.matches("^(9|10|11|12)$|^Grade (9|10|11|12)$|^High.*");
    }
    
    public boolean isUniversity() {
        return value.matches("^(Freshman|Sophomore|Junior|Senior|Graduate|PhD).*|^Year [1-4]$|^University.*");
    }
}