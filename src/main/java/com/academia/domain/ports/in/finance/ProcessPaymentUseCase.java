package com.academia.domain.ports.in.finance;

import com.academia.domain.ports.in.commands.ProcessPaymentCommand;
import com.academia.domain.ports.in.dtos.PaymentDetailsDTO;

/**
 * Use case for processing payments for fees.
 */
public interface ProcessPaymentUseCase {
    
    /**
     * Processes a payment for the specified fee.
     * 
     * @param command the command containing payment details
     * @return the payment details
     * @throws IllegalArgumentException if the command is invalid
     * @throws IllegalStateException if the fee cannot accept payments
     */
    PaymentDetailsDTO processPayment(ProcessPaymentCommand command);
}