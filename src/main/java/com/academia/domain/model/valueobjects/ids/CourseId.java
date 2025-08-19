package com.academia.domain.model.valueobjects.ids;

import lombok.Value;

@Value
public class CourseId {
    Long value;
    
    public CourseId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("CourseId cannot be null");
        }
        this.value = value;
    }
    
    public static CourseId of(Long value) {
        return new CourseId(value);
    }
}