package com.academia.domain.model.valueobjects.ids;

import lombok.Value;

@Value
public class FeeTemplateId {
    Long value;
    
    public FeeTemplateId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("FeeTemplateId cannot be null");
        }
        this.value = value;
    }
    
    public static FeeTemplateId of(Long value) {
        return new FeeTemplateId(value);
    }
}