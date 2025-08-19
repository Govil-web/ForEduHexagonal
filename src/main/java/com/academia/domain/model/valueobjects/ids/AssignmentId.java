package com.academia.domain.model.valueobjects.ids;

import lombok.Value;

@Value
public class AssignmentId {
    Long value;
    
    public AssignmentId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("AssignmentId cannot be null");
        }
        this.value = value;
    }
    
    public static AssignmentId of(Long value) {
        return new AssignmentId(value);
    }
}