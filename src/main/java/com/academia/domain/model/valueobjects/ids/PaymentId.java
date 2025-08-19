package com.academia.domain.model.valueobjects.ids;

import lombok.Value;

@Value
public class PaymentId {
    Long value;
    
    public PaymentId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("PaymentId cannot be null");
        }
        this.value = value;
    }
    
    public static PaymentId of(Long value) {
        return new PaymentId(value);
    }
}