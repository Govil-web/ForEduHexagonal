package com.academia.domain.model.valueobjects.ids;

import lombok.Value;


/**
 * Value object representing a unique identifier for an AcademicTerm aggregate.
 */
@Value
public class AcademicTermId {
    Long value;
    
    public AcademicTermId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("Academic Term ID cannot be null");
        }
        this.value = value;
    }
    
    public static AcademicTermId of(Long value) {
        return new AcademicTermId(value);
    }
}