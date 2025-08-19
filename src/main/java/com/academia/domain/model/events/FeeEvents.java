package com.academia.domain.model.events;

import com.academia.domain.model.valueobjects.ids.FeeId;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.financial.Money;
import com.academia.domain.model.enums.FeeType;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain events related to Fee aggregate lifecycle and business operations.
 */
public class FeeEvents {
    
    /**
     * Event published when a new fee is created for a student.
     */
    public record FeeCreatedEvent(
        UUID eventId,
        Instant occurredOn,
        FeeId feeId,
        AccountId studentId,
        OrganizationId organizationId,
        FeeType feeType,
        Money amount
    ) implements DomainEvent {
        public FeeCreatedEvent(FeeId feeId, AccountId studentId, OrganizationId organizationId, FeeType feeType, Money amount) {
            this(UUID.randomUUID(), Instant.now(), feeId, studentId, organizationId, feeType, amount);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    /**
     * Event published when a fee becomes due.
     */
    public record FeeBecameDueEvent(
        UUID eventId,
        Instant occurredOn,
        FeeId feeId,
        AccountId studentId,
        Money amount
    ) implements DomainEvent {
        public FeeBecameDueEvent(FeeId feeId, AccountId studentId, Money amount) {
            this(UUID.randomUUID(), Instant.now(), feeId, studentId, amount);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    /**
     * Event published when a fee becomes overdue.
     */
    public record FeeBecameOverdueEvent(
        UUID eventId,
        Instant occurredOn,
        FeeId feeId,
        AccountId studentId,
        Money outstandingAmount,
        int daysPastDue
    ) implements DomainEvent {
        public FeeBecameOverdueEvent(FeeId feeId, AccountId studentId, Money outstandingAmount, int daysPastDue) {
            this(UUID.randomUUID(), Instant.now(), feeId, studentId, outstandingAmount, daysPastDue);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    /**
     * Event published when a partial payment is made towards a fee.
     */
    public record FeePartiallyPaidEvent(
        UUID eventId,
        Instant occurredOn,
        FeeId feeId,
        AccountId studentId,
        Money paymentAmount,
        Money remainingBalance
    ) implements DomainEvent {
        public FeePartiallyPaidEvent(FeeId feeId, AccountId studentId, Money paymentAmount, Money remainingBalance) {
            this(UUID.randomUUID(), Instant.now(), feeId, studentId, paymentAmount, remainingBalance);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    /**
     * Event published when a fee is fully paid.
     */
    public record FeeFullyPaidEvent(
        UUID eventId,
        Instant occurredOn,
        FeeId feeId,
        AccountId studentId,
        Money totalAmount
    ) implements DomainEvent {
        public FeeFullyPaidEvent(FeeId feeId, AccountId studentId, Money totalAmount) {
            this(UUID.randomUUID(), Instant.now(), feeId, studentId, totalAmount);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    /**
     * Event published when a fee is waived by administration.
     */
    public record FeeWaivedEvent(
        UUID eventId,
        Instant occurredOn,
        FeeId feeId,
        AccountId studentId,
        Money waivedAmount,
        String reason,
        AccountId approvedBy
    ) implements DomainEvent {
        public FeeWaivedEvent(FeeId feeId, AccountId studentId, Money waivedAmount, String reason, AccountId approvedBy) {
            this(UUID.randomUUID(), Instant.now(), feeId, studentId, waivedAmount, reason, approvedBy);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    /**
     * Event published when a fee is cancelled.
     */
    public record FeeCancelledEvent(
        UUID eventId,
        Instant occurredOn,
        FeeId feeId,
        AccountId studentId,
        String reason,
        AccountId cancelledBy
    ) implements DomainEvent {
        public FeeCancelledEvent(FeeId feeId, AccountId studentId, String reason, AccountId cancelledBy) {
            this(UUID.randomUUID(), Instant.now(), feeId, studentId, reason, cancelledBy);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
    
    /**
     * Event published when a fee is refunded.
     */
    public record FeeRefundedEvent(
        UUID eventId,
        Instant occurredOn,
        FeeId feeId,
        AccountId studentId,
        Money refundAmount,
        String reason
    ) implements DomainEvent {
        public FeeRefundedEvent(FeeId feeId, AccountId studentId, Money refundAmount, String reason) {
            this(UUID.randomUUID(), Instant.now(), feeId, studentId, refundAmount, reason);
        }

        @Override
        public UUID getEventId() { return eventId(); }

        @Override
        public Instant getOccurredOn() { return occurredOn(); }
    }
}