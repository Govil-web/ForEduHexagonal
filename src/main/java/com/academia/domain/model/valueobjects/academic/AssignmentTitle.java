package com.academia.domain.model.valueobjects.academic;

import lombok.Value;

/**
 * Value object representing an assignment title with validation.
 */
@Value
public class AssignmentTitle {
    String value;
    
    public AssignmentTitle(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Assignment title cannot be null or empty");
        }
        if (value.length() > 200) {
            throw new IllegalArgumentException("Assignment title cannot exceed 200 characters");
        }
        this.value = value.trim();
    }
    
    public static AssignmentTitle of(String title) {
        return new AssignmentTitle(title);
    }
}