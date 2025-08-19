package com.academia.domain.model.valueobjects.academic;

import lombok.Value;

/**
 * Value object representing a unique subject code within an organization.
 * Subject codes follow organizational standards and must be unique per organization.
 */
@Value
public class SubjectCode {
    String value;
    
    public SubjectCode(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject code cannot be null or empty");
        }
        if (value.length() > 20) {
            throw new IllegalArgumentException("Subject code cannot exceed 20 characters");
        }
        if (!value.matches("^[A-Z0-9-_]+$")) {
            throw new IllegalArgumentException("Subject code must contain only uppercase letters, numbers, hyphens, and underscores");
        }
        this.value = value.trim().toUpperCase();
    }
}