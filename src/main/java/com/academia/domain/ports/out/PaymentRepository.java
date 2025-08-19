package com.academia.domain.ports.out;

import com.academia.domain.model.aggregates.Payment;
import com.academia.domain.model.valueobjects.ids.PaymentId;
import com.academia.domain.model.valueobjects.ids.FeeId;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.enums.PaymentStatus;
import com.academia.domain.model.enums.PaymentMethod;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Payment aggregate persistence operations.
 */
public interface PaymentRepository {
    
    /**
     * Saves a payment to the repository.
     * 
     * @param payment the payment to save
     * @return the saved payment
     */
    Payment save(Payment payment);
    
    /**
     * Finds a payment by its ID.
     * 
     * @param paymentId the payment ID
     * @return an optional containing the payment if found
     */
    Optional<Payment> findById(PaymentId paymentId);
    
    /**
     * Finds a payment by transaction ID.
     * 
     * @param transactionId the transaction ID
     * @return an optional containing the payment if found
     */
    Optional<Payment> findByTransactionId(String transactionId);
    
    /**
     * Finds all payments for a specific fee.
     * 
     * @param feeId the fee ID
     * @return list of payments for the fee
     */
    List<Payment> findByFeeId(FeeId feeId);
    
    /**
     * Finds all payments made by a specific payer.
     * 
     * @param payerId the payer ID
     * @return list of payments made by the payer
     */
    List<Payment> findByPayerId(AccountId payerId);
    
    /**
     * Finds payments with a specific status.
     * 
     * @param status the payment status
     * @return list of payments with the specified status
     */
    List<Payment> findByStatus(PaymentStatus status);
    
    /**
     * Finds payments by method.
     * 
     * @param method the payment method
     * @return list of payments using the specified method
     */
    List<Payment> findByMethod(PaymentMethod method);
    
    /**
     * Finds payments within a date range.
     * 
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of payments within the date range
     */
    List<Payment> findPaymentsBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Finds pending payments that are older than the specified time.
     * 
     * @param cutoffTime the cutoff time
     * @return list of stale pending payments
     */
    List<Payment> findStalePendingPayments(LocalDateTime cutoffTime);
    
    /**
     * Finds successful payments for a specific fee.
     * 
     * @param feeId the fee ID
     * @return list of successful payments for the fee
     */
    List<Payment> findSuccessfulPaymentsByFee(FeeId feeId);
    
    /**
     * Deletes a payment from the repository.
     * 
     * @param paymentId the payment ID to delete
     */
    void deleteById(PaymentId paymentId);
}