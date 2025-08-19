package com.academia.domain.ports.in.commands;

import com.academia.domain.model.valueobjects.ids.FeeId;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.enums.PaymentMethod;

import java.math.BigDecimal;

/**
 * Command to process a payment for a fee.
 */
public record ProcessPaymentCommand(
    FeeId feeId,
    AccountId payerId,
    BigDecimal amount,
    String currencyCode,
    PaymentMethod paymentMethod,
    String paymentDetails
) {
    public ProcessPaymentCommand {
        if (feeId == null) {
            throw new IllegalArgumentException("Fee ID cannot be null");
        }
        if (payerId == null) {
            throw new IllegalArgumentException("Payer ID cannot be null");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency code cannot be null or empty");
        }
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }
    }
}