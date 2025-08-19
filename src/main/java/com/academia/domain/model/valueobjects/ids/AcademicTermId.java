package com.academia.domain.model.valueobjects.ids;

import lombok.Value;

import java.util.UUID;

/**
 * Value object representing a unique identifier for an AcademicTerm aggregate.
 */
@Value
public class AcademicTermId {
    UUID value;

    public AcademicTermId() {
        this(UUID.randomUUID());
    }
    
    public AcademicTermId(UUID value) {
        if (value == null || value.toString().isEmpty()) {
            throw new IllegalArgumentException("Academic Term ID must be a positive number");
        }
        this.value = value;
    }
    
    public static AcademicTermId of(UUID value) {
        return new AcademicTermId(value);
    }
}