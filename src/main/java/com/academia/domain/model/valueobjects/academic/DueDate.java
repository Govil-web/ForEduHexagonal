package com.academia.domain.model.valueobjects.academic;

import lombok.Value;
import java.time.LocalDateTime;

/**
 * Value object representing a due date for assignments with validation.
 */
@Value
public class DueDate {
    LocalDateTime value;
    
    public DueDate(LocalDateTime value) {
        if (value == null) {
            throw new IllegalArgumentException("Due date cannot be null");
        }
        if (value.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Due date cannot be in the past");
        }
        this.value = value;
    }
    
    public boolean isPast() {
        return value.isBefore(LocalDateTime.now());
    }
    
    public boolean isWithinHours(int hours) {
        return value.isBefore(LocalDateTime.now().plusHours(hours));
    }
    
    public static DueDate of(LocalDateTime dateTime) {
        return new DueDate(dateTime);
    }
}