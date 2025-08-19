package com.academia.domain.model.valueobjects.ids;

import lombok.Value;
import java.util.UUID;

@Value
public class FeeTemplateId {
    UUID value;
    
    public FeeTemplateId(String value) {
        this(UUID.fromString(value));
    }
    
    public FeeTemplateId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("FeeTemplateId cannot be null");
        }
        this.value = value;
    }
    
    public static FeeTemplateId of(String value) {
        return new FeeTemplateId(value);
    }
    
    public static FeeTemplateId of(UUID value) {
        return new FeeTemplateId(value);
    }
}