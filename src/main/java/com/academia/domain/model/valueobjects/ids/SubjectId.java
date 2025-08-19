package com.academia.domain.model.valueobjects.ids;

import lombok.Value;
import java.util.UUID;

/**
 * Value object representing a unique identifier for a Subject aggregate.
 */
@Value
public class SubjectId {
    UUID value;
    
    public SubjectId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("Subject ID cannot be null");
        }
        this.value = value;
    }
    
    // Constructor for generating new IDs
    public SubjectId() {
        this.value = UUID.randomUUID();
    }
    
    public static SubjectId of(UUID value) {
        return new SubjectId(value);
    }
    
    public static SubjectId newId() {
        return new SubjectId();
    }
}
