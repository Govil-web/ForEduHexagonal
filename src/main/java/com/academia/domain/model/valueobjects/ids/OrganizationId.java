package com.academia.domain.model.valueobjects.ids;

import lombok.Value;

@Value
public class OrganizationId {
    Long value;
    
    public OrganizationId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("Organization ID cannot be null");
        }
        this.value = value;
    }
    
    public static OrganizationId of(Long value) {
        return new OrganizationId(value);
    }
}