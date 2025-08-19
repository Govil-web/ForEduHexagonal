package com.academia.domain.model.valueobjects.ids;

import lombok.Value;
import java.util.UUID;

@Value
public class PaymentId {
    UUID value;
    
    public PaymentId(String value) {
        this(UUID.fromString(value));
    }
    
    public PaymentId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("PaymentId cannot be null");
        }
        this.value = value;
    }
    
    public static PaymentId of(String value) {
        return new PaymentId(value);
    }
    
    public static PaymentId of(UUID value) {
        return new PaymentId(value);
    }
}