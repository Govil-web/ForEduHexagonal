package com.academia.domain.model.valueobjects.ids;

import lombok.Value;

/**
 * Value object representing a unique identifier for a Subject aggregate.
 */
@Value
public class SubjectId {
    Long value;
    
    public SubjectId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("Subject ID cannot be null");
        }
        this.value = value;
    }
    
    public static SubjectId of(Long value) {
        return new SubjectId(value);
    }
}
