package com.academia.domain.model.valueobjects.ids;

import lombok.Value;

@Value
public class FeeId {
    Long value;
    
    public FeeId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("FeeId cannot be null");
        }
        this.value = value;
    }
    
    public static FeeId of(Long value) {
        return new FeeId(value);
    }
}