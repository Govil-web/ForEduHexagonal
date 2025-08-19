package com.academia.domain.model.valueobjects.financial;

import lombok.Value;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

/**
 * Value object representing a monetary amount with currency.
 * Ensures financial calculations are precise and currency-aware.
 */
@Value
public class Money {
    BigDecimal amount;
    Currency currency;
    
    public Money(BigDecimal amount, Currency currency) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Currency cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        
        // Round to currency's default fraction digits
        this.amount = amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
        this.currency = currency;
    }
    
    public static Money of(double amount, String currencyCode) {
        return new Money(BigDecimal.valueOf(amount), Currency.getInstance(currencyCode));
    }
    
    public static Money of(BigDecimal amount, String currencyCode) {
        return new Money(amount, Currency.getInstance(currencyCode));
    }
    
    public static Money zero(String currencyCode) {
        return new Money(BigDecimal.ZERO, Currency.getInstance(currencyCode));
    }
    
    /**
     * Adds another money amount. Both amounts must have the same currency.
     */
    public Money add(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add amounts with different currencies");
        }
        return new Money(amount.add(other.amount), currency);
    }
    
    /**
     * Subtracts another money amount. Both amounts must have the same currency.
     */
    public Money subtract(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot subtract amounts with different currencies");
        }
        BigDecimal result = amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Result cannot be negative");
        }
        return new Money(result, currency);
    }
    
    /**
     * Multiplies the amount by a factor.
     */
    public Money multiply(BigDecimal factor) {
        if (factor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Factor cannot be negative");
        }
        return new Money(amount.multiply(factor), currency);
    }
    
    /**
     * Checks if this money amount is greater than another.
     */
    public boolean isGreaterThan(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot compare amounts with different currencies");
        }
        return amount.compareTo(other.amount) > 0;
    }
    
    /**
     * Checks if this money amount is greater than or equal to another.
     */
    public boolean isGreaterThanOrEqual(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot compare amounts with different currencies");
        }
        return amount.compareTo(other.amount) >= 0;
    }
    
    /**
     * Checks if this money amount is zero.
     */
    public boolean isZero() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }
    
    /**
     * Returns the currency code.
     */
    public String getCurrencyCode() {
        return currency.getCurrencyCode();
    }
}