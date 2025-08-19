package com.academia.domain.model.valueobjects.ids;

import lombok.Value;

@Value
public class AccountId {
    Long value;
    
    public AccountId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("Account ID cannot be null");
        }
        this.value = value;
    }
    
    public static AccountId of(Long value) {
        return new AccountId(value);
    }
}