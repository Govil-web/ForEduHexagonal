package com.academia.domain.model.enums;

/**
 * Enumeration representing different payment methods available in the system.
 */
public enum PaymentMethod {
    CREDIT_CARD("Credit card payment"),
    DEBIT_CARD("Debit card payment"),
    BANK_TRANSFER("Bank wire transfer"),
    CASH("Cash payment"),
    CHECK("Check payment"),
    SCHOLARSHIP("Scholarship credit"),
    FINANCIAL_AID("Financial aid credit"),
    INSTALLMENT_PLAN("Installment payment plan"),
    ONLINE_PAYMENT("Online payment gateway"),
    MOBILE_PAYMENT("Mobile payment app");
    
    private final String description;
    
    PaymentMethod(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isElectronic() {
        return this == CREDIT_CARD || this == DEBIT_CARD || this == BANK_TRANSFER || 
               this == ONLINE_PAYMENT || this == MOBILE_PAYMENT;
    }
    
    public boolean requiresVerification() {
        return this == CHECK || this == BANK_TRANSFER;
    }
    
    public boolean isCredit() {
        return this == SCHOLARSHIP || this == FINANCIAL_AID;
    }
}