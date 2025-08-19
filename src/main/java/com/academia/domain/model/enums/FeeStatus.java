package com.academia.domain.model.enums;

/**
 * Enumeration representing the status of fees in the system.
 */
public enum FeeStatus {
    PENDING("Fee has been generated but not yet due"),
    DUE("Fee is currently due for payment"),
    OVERDUE("Fee is past its due date"),
    PARTIALLY_PAID("Fee has been partially paid"),
    PAID("Fee has been fully paid"),
    WAIVED("Fee has been waived by administration"),
    CANCELLED("Fee has been cancelled"),
    REFUNDED("Fee has been refunded to the student");
    
    private final String description;
    
    FeeStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean canAcceptPayment() {
        return this == PENDING || this == DUE || this == OVERDUE || this == PARTIALLY_PAID;
    }
    
    public boolean isCompleted() {
        return this == PAID || this == WAIVED || this == CANCELLED || this == REFUNDED;
    }
    
    public boolean requiresPayment() {
        return this == DUE || this == OVERDUE || this == PARTIALLY_PAID;
    }
}