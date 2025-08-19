package com.academia.domain.model.valueobjects.ids;

import lombok.Value;

import java.util.UUID;

@Value
public class OrganizationId {
    UUID value;
    
    public OrganizationId(String value) {
        this(UUID.fromString(value));
    }
    
    public OrganizationId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("Organization ID cannot be null");
        }
        this.value = value;
    }
    
    public static OrganizationId of(String value) {
        return new OrganizationId(value);
    }
    
    public static OrganizationId of(UUID value) {
        return new OrganizationId(value);
    }
}