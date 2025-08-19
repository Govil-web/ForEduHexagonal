package com.academia.domain.model.valueobjects.ids;

import lombok.Value;
import java.util.UUID;

@Value
public class AssignmentId {
    UUID value;
    
    public AssignmentId(String value) {
        this(UUID.fromString(value));
    }
    
    public AssignmentId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("AssignmentId cannot be null");
        }
        this.value = value;
    }
    
    public static AssignmentId of(String value) {
        return new AssignmentId(value);
    }
    
    public static AssignmentId of(UUID value) {
        return new AssignmentId(value);
    }
}