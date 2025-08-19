package com.academia.domain.model.aggregates;

import com.academia.domain.model.events.DomainEvent;
import com.academia.domain.model.events.PaymentEvents;
import com.academia.domain.model.valueobjects.ids.PaymentId;
import com.academia.domain.model.valueobjects.ids.FeeId;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.financial.Money;
import com.academia.domain.model.enums.PaymentMethod;
import com.academia.domain.model.enums.PaymentStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Payment aggregate root representing a payment transaction for a fee.
 * Manages payment lifecycle, status transitions, and financial operations.
 */
@Getter
public class Payment {
    private final PaymentId id;
    private final FeeId feeId;
    private final AccountId payerId;
    private final Money amount;
    private final PaymentMethod method;
    private PaymentStatus status;
    private String transactionId;
    private String gatewayResponse;
    private String failureReason;
    private Money refundedAmount;
    private final LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private LocalDateTime updatedAt;
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    
    public Payment(PaymentId id, FeeId feeId, AccountId payerId, Money amount, PaymentMethod method) {
        this.id = id;
        this.feeId = feeId;
        this.payerId = payerId;
        this.amount = amount;
        this.method = method;
        this.status = PaymentStatus.PENDING;
        this.refundedAmount = Money.zero(amount.getCurrencyCode());
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new PaymentEvents.PaymentInitiatedEvent(id, feeId, payerId, amount, method));
    }
    
    /**
     * Marks the payment as successfully completed.
     */
    public void markAsCompleted(String transactionId, String gatewayResponse) {
        if (status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Can only complete pending payments");
        }
        
        this.status = PaymentStatus.COMPLETED;
        this.transactionId = transactionId;
        this.gatewayResponse = gatewayResponse;
        this.processedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new PaymentEvents.PaymentCompletedEvent(id, feeId, payerId, amount, method, transactionId));
    }
    
    /**
     * Marks the payment as failed.
     */
    public void markAsFailed(String failureReason, String gatewayResponse) {
        if (status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Can only fail pending payments");
        }
        
        this.status = PaymentStatus.FAILED;
        this.failureReason = failureReason;
        this.gatewayResponse = gatewayResponse;
        this.processedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new PaymentEvents.PaymentFailedEvent(id, feeId, payerId, amount, failureReason));
    }
    
    /**
     * Cancels the payment.
     */
    public void cancel(String reason) {
        if (status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Can only cancel pending payments");
        }
        
        this.status = PaymentStatus.CANCELLED;
        this.failureReason = reason;
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new PaymentEvents.PaymentCancelledEvent(id, feeId, payerId, reason));
    }
    
    /**
     * Processes a refund for this payment.
     */
    public void refund(Money refundAmount, String reason) {
        if (status != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Can only refund completed payments");
        }
        
        Money totalRefundable = amount.subtract(refundedAmount);
        if (refundAmount.isGreaterThan(totalRefundable)) {
            throw new IllegalArgumentException("Refund amount exceeds refundable amount");
        }
        
        this.refundedAmount = this.refundedAmount.add(refundAmount);
        this.status = refundedAmount.isGreaterThanOrEqual(amount) ? PaymentStatus.REFUNDED : PaymentStatus.PARTIAL_REFUND;
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new PaymentEvents.PaymentRefundedEvent(id, feeId, payerId, refundAmount, reason));
    }
    
    /**
     * Marks the payment as disputed.
     */
    public void markAsDisputed(String disputeReason) {
        if (status != PaymentStatus.COMPLETED && status != PaymentStatus.PARTIAL_REFUND) {
            throw new IllegalStateException("Can only dispute completed or partially refunded payments");
        }
        
        this.status = PaymentStatus.DISPUTED;
        this.failureReason = disputeReason;
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new PaymentEvents.PaymentDisputedEvent(id, feeId, payerId, disputeReason));
    }
    
    /**
     * Processes a chargeback for this payment.
     */
    public void processChargeback(String chargebackReason) {
        if (status != PaymentStatus.COMPLETED && status != PaymentStatus.PARTIAL_REFUND) {
            throw new IllegalStateException("Can only process chargeback for completed or partially refunded payments");
        }
        
        this.status = PaymentStatus.CHARGEBACK;
        this.failureReason = chargebackReason;
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new PaymentEvents.PaymentChargebackEvent(id, feeId, payerId, amount, chargebackReason));
    }
    
    /**
     * Gets the net amount after refunds.
     */
    public Money getNetAmount() {
        return amount.subtract(refundedAmount);
    }
    
    /**
     * Gets the remaining refundable amount.
     */
    public Money getRefundableAmount() {
        if (status != PaymentStatus.COMPLETED && status != PaymentStatus.PARTIAL_REFUND) {
            return Money.zero(amount.getCurrencyCode());
        }
        return amount.subtract(refundedAmount);
    }
    
    /**
     * Checks if the payment is successful.
     */
    public boolean isSuccessful() {
        return status.isSuccessful();
    }
    
    /**
     * Checks if the payment is in a final state.
     */
    public boolean isFinal() {
        return status.isFinal();
    }
    
    /**
     * Checks if the payment can be refunded.
     */
    public boolean canBeRefunded() {
        return status.canBeRefunded() && getRefundableAmount().isGreaterThan(Money.zero(amount.getCurrencyCode()));
    }
    
    /**
     * Checks if the payment is in progress.
     */
    public boolean isInProgress() {
        return status.isInProgress();
    }
    
    public List<DomainEvent> getDomainEvents() {
        return List.copyOf(domainEvents);
    }
    
    public void clearDomainEvents() {
        domainEvents.clear();
    }
}