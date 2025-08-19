package com.academia.domain.model.valueobjects.ids;

import lombok.Value;
import java.util.UUID;

@Value
public class CourseId {
    UUID value;
    
    public CourseId(String value) {
        this(UUID.fromString(value));
    }
    
    public CourseId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("CourseId cannot be null");
        }
        this.value = value;
    }
    
    public static CourseId of(String value) {
        return new CourseId(value);
    }
    
    public static CourseId of(UUID value) {
        return new CourseId(value);
    }
}