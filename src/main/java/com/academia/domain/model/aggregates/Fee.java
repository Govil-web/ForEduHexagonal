package com.academia.domain.model.aggregates;

import com.academia.domain.model.events.DomainEvent;
import com.academia.domain.model.events.FeeEvents;
import com.academia.domain.model.valueobjects.ids.FeeId;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.financial.Money;
import com.academia.domain.model.enums.FeeType;
import com.academia.domain.model.enums.FeeStatus;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Fee aggregate root representing a financial charge for a student within an organization.
 * Manages fee lifecycle, payments, waivers, and status transitions.
 */
@Getter
public class Fee {
    private final FeeId id;
    private final AccountId studentId;
    private final OrganizationId organizationId;
    private final FeeType feeType;
    private final Money originalAmount;
    private Money paidAmount;
    private FeeStatus status;
    private final LocalDate dueDate;
    private String description;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    
    public Fee(FeeId id, AccountId studentId, OrganizationId organizationId, 
              FeeType feeType, Money amount, LocalDate dueDate, String description) {
        if (dueDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Due date cannot be in the past");
        }
        
        this.id = id;
        this.studentId = studentId;
        this.organizationId = organizationId;
        this.feeType = feeType;
        this.originalAmount = amount;
        this.paidAmount = Money.zero(amount.getCurrencyCode());
        this.status = LocalDate.now().isBefore(dueDate) ? FeeStatus.PENDING : FeeStatus.DUE;
        this.dueDate = dueDate;
        this.description = description != null ? description.trim() : "";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new FeeEvents.FeeCreatedEvent(id, studentId, organizationId, feeType, amount));
    }
    
    /**
     * Records a payment towards this fee.
     */
    public void recordPayment(Money paymentAmount) {
        if (!status.canAcceptPayment()) {
            throw new IllegalStateException("Cannot accept payment for fee in status: " + status);
        }
        
        Money remainingBalance = getRemainingBalance();
        if (paymentAmount.isGreaterThan(remainingBalance)) {
            throw new IllegalArgumentException("Payment amount exceeds remaining balance");
        }
        
        this.paidAmount = this.paidAmount.add(paymentAmount);
        Money newRemainingBalance = getRemainingBalance();
        this.updatedAt = LocalDateTime.now();
        
        if (newRemainingBalance.isZero()) {
            this.status = FeeStatus.PAID;
            domainEvents.add(new FeeEvents.FeeFullyPaidEvent(id, studentId, originalAmount));
        } else {
            this.status = FeeStatus.PARTIALLY_PAID;
            domainEvents.add(new FeeEvents.FeePartiallyPaidEvent(id, studentId, paymentAmount, newRemainingBalance));
        }
    }
    
    /**
     * Waives this fee partially or fully by administration.
     */
    public void waive(Money waiverAmount, String reason, AccountId approvedBy) {
        if (status.isCompleted()) {
            throw new IllegalStateException("Cannot waive a completed fee");
        }
        
        Money remainingBalance = getRemainingBalance();
        if (waiverAmount.isGreaterThan(remainingBalance)) {
            throw new IllegalArgumentException("Waiver amount exceeds remaining balance");
        }
        
        this.paidAmount = this.paidAmount.add(waiverAmount);
        this.status = getRemainingBalance().isZero() ? FeeStatus.WAIVED : FeeStatus.PARTIALLY_PAID;
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new FeeEvents.FeeWaivedEvent(id, studentId, waiverAmount, reason, approvedBy));
    }
    
    /**
     * Cancels this fee before it's paid.
     */
    public void cancel(String reason, AccountId cancelledBy) {
        if (status == FeeStatus.PAID || status == FeeStatus.PARTIALLY_PAID) {
            throw new IllegalStateException("Cannot cancel a fee that has been paid");
        }
        
        this.status = FeeStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new FeeEvents.FeeCancelledEvent(id, studentId, reason, cancelledBy));
    }
    
    /**
     * Processes a refund for this fee.
     */
    public void refund(Money refundAmount, String reason) {
        if (status != FeeStatus.PAID) {
            throw new IllegalStateException("Can only refund fully paid fees");
        }
        
        if (refundAmount.isGreaterThan(paidAmount)) {
            throw new IllegalArgumentException("Refund amount exceeds paid amount");
        }
        
        this.paidAmount = this.paidAmount.subtract(refundAmount);
        this.status = paidAmount.isZero() ? FeeStatus.REFUNDED : FeeStatus.PARTIALLY_PAID;
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new FeeEvents.FeeRefundedEvent(id, studentId, refundAmount, reason));
    }
    
    /**
     * Marks the fee as due when the due date is reached.
     */
    public void markAsDue() {
        if (status == FeeStatus.PENDING && !dueDate.isAfter(LocalDate.now())) {
            this.status = FeeStatus.DUE;
            this.updatedAt = LocalDateTime.now();
            domainEvents.add(new FeeEvents.FeeBecameDueEvent(id, studentId, getRemainingBalance()));
        }
    }
    
    /**
     * Marks the fee as overdue when it's past the due date.
     */
    public void markAsOverdue() {
        if ((status == FeeStatus.DUE || status == FeeStatus.PARTIALLY_PAID) && isOverdue()) {
            this.status = FeeStatus.OVERDUE;
            this.updatedAt = LocalDateTime.now();
            
            int daysPastDue = (int) ChronoUnit.DAYS.between(dueDate, LocalDate.now());
            domainEvents.add(new FeeEvents.FeeBecameOverdueEvent(id, studentId, getRemainingBalance(), daysPastDue));
        }
    }
    
    /**
     * Calculates the remaining balance on this fee.
     */
    public Money getRemainingBalance() {
        return originalAmount.subtract(paidAmount);
    }
    
    /**
     * Checks if the fee is overdue.
     */
    public boolean isOverdue() {
        return LocalDate.now().isAfter(dueDate) && status.requiresPayment();
    }
    
    /**
     * Checks if the fee is fully paid.
     */
    public boolean isFullyPaid() {
        return status == FeeStatus.PAID || status == FeeStatus.WAIVED;
    }
    
    /**
     * Checks if the fee requires payment.
     */
    public boolean requiresPayment() {
        return status.requiresPayment();
    }
    
    /**
     * Gets the number of days past due.
     */
    public int getDaysPastDue() {
        if (!isOverdue()) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }
    
    /**
     * Updates the fee description.
     */
    public void updateDescription(String newDescription) {
        this.description = newDescription != null ? newDescription.trim() : "";
        this.updatedAt = LocalDateTime.now();
    }
    
    public List<DomainEvent> getDomainEvents() {
        return List.copyOf(domainEvents);
    }
    
    public void clearDomainEvents() {
        domainEvents.clear();
    }
}