package com.academia.domain.model.valueobjects.ids;

import lombok.Value;
import java.util.UUID;

@Value
public class FeeId {
    UUID value;
    
    public FeeId(String value) {
        this(UUID.fromString(value));
    }
    
    public FeeId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("FeeId cannot be null");
        }
        this.value = value;
    }
    
    public static FeeId of(String value) {
        return new FeeId(value);
    }
    
    public static FeeId of(UUID value) {
        return new FeeId(value);
    }
}