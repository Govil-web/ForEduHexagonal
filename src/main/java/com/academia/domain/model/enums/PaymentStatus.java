package com.academia.domain.model.enums;

/**
 * Enumeration representing the status of payments in the system.
 */
public enum PaymentStatus {
    PENDING("Payment is being processed"),
    COMPLETED("Payment has been successfully completed"),
    FAILED("Payment processing failed"),
    CANCELLED("Payment was cancelled"),
    REFUNDED("Payment has been refunded"),
    PARTIAL_REFUND("Payment has been partially refunded"),
    CHARGEBACK("Payment was charged back"),
    DISPUTED("Payment is under dispute");
    
    private final String description;
    
    PaymentStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isSuccessful() {
        return this == COMPLETED;
    }
    
    public boolean isFinal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED || this == REFUNDED;
    }
    
    public boolean canBeRefunded() {
        return this == COMPLETED;
    }
    
    public boolean isInProgress() {
        return this == PENDING;
    }
}