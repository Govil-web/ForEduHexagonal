package com.academia.domain.model.events;

import com.academia.domain.model.valueobjects.ids.PaymentId;
import com.academia.domain.model.valueobjects.ids.FeeId;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.financial.Money;
import com.academia.domain.model.enums.PaymentMethod;

import java.time.Instant;


/**
 * Domain events related to Payment aggregate lifecycle and business operations.
 */
public class PaymentEvents {
    
    public record PaymentInitiatedEvent(
        Long eventId,
        Instant occurredOn,
        PaymentId paymentId,
        FeeId feeId,
        AccountId payerId,
        Money amount,
        PaymentMethod method
    ) implements DomainEvent {
        public PaymentInitiatedEvent(PaymentId paymentId, FeeId feeId, AccountId payerId, Money amount, PaymentMethod method) {
            this(null, Instant.now(), paymentId, feeId, payerId, amount, method);
        }

        @Override
        public Long getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record PaymentCompletedEvent(
        Long eventId,
        Instant occurredOn,
        PaymentId paymentId,
        FeeId feeId,
        AccountId payerId,
        Money amount,
        PaymentMethod method,
        String transactionId
    ) implements DomainEvent {
        public PaymentCompletedEvent(PaymentId paymentId, FeeId feeId, AccountId payerId, Money amount, PaymentMethod method, String transactionId) {
            this(null, Instant.now(), paymentId, feeId, payerId, amount, method, transactionId);
        }

        @Override
        public Long getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record PaymentFailedEvent(
        Long eventId,
        Instant occurredOn,
        PaymentId paymentId,
        FeeId feeId,
        AccountId payerId,
        Money amount,
        String failureReason
    ) implements DomainEvent {
        public PaymentFailedEvent(PaymentId paymentId, FeeId feeId, AccountId payerId, Money amount, String failureReason) {
            this(null, Instant.now(), paymentId, feeId, payerId, amount, failureReason);
        }

        @Override
        public Long getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record PaymentCancelledEvent(
        Long eventId,
        Instant occurredOn,
        PaymentId paymentId,
        FeeId feeId,
        AccountId payerId,
        String reason
    ) implements DomainEvent {
        public PaymentCancelledEvent(PaymentId paymentId, FeeId feeId, AccountId payerId, String reason) {
            this(null, Instant.now(), paymentId, feeId, payerId, reason);
        }

        @Override
        public Long getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record PaymentRefundedEvent(
        Long eventId,
        Instant occurredOn,
        PaymentId paymentId,
        FeeId feeId,
        AccountId payerId,
        Money refundAmount,
        String reason
    ) implements DomainEvent {
        public PaymentRefundedEvent(PaymentId paymentId, FeeId feeId, AccountId payerId, Money refundAmount, String reason) {
            this(null, Instant.now(), paymentId, feeId, payerId, refundAmount, reason);
        }

        @Override
        public Long getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record PaymentDisputedEvent(
        Long eventId,
        Instant occurredOn,
        PaymentId paymentId,
        FeeId feeId,
        AccountId payerId,
        String disputeReason
    ) implements DomainEvent {
        public PaymentDisputedEvent(PaymentId paymentId, FeeId feeId, AccountId payerId, String disputeReason) {
            this(null, Instant.now(), paymentId, feeId, payerId, disputeReason);
        }

        @Override
        public Long getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    public record PaymentChargebackEvent(
        Long eventId,
        Instant occurredOn,
        PaymentId paymentId,
        FeeId feeId,
        AccountId payerId,
        Money chargebackAmount,
        String reason
    ) implements DomainEvent {
        public PaymentChargebackEvent(PaymentId paymentId, FeeId feeId, AccountId payerId, Money chargebackAmount, String reason) {
            this(null, Instant.now(), paymentId, feeId, payerId, chargebackAmount, reason);
        }

        @Override
        public Long getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
}