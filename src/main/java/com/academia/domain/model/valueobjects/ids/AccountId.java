package com.academia.domain.model.valueobjects.ids;

import lombok.Value;

import java.util.UUID;

@Value
public class AccountId {
    UUID value;
    
    public AccountId(String value) {
        this(UUID.fromString(value));
    }
    
    public AccountId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("Account ID cannot be null");
        }
        this.value = value;
    }
    
    public static AccountId of(String value) {
        return new AccountId(value);
    }
    
    public static AccountId of(UUID value) {
        return new AccountId(value);
    }
}