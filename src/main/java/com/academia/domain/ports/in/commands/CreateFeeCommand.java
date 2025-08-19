package com.academia.domain.ports.in.commands;

import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.enums.FeeType;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Command to create a new fee for a student.
 */
public record CreateFeeCommand(
    AccountId studentId,
    OrganizationId organizationId,
    FeeType feeType,
    BigDecimal amount,
    String currencyCode,
    LocalDate dueDate,
    String description
) {
    public CreateFeeCommand {
        if (studentId == null) {
            throw new IllegalArgumentException("Student ID cannot be null");
        }
        if (organizationId == null) {
            throw new IllegalArgumentException("Organization ID cannot be null");
        }
        if (feeType == null) {
            throw new IllegalArgumentException("Fee type cannot be null");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency code cannot be null or empty");
        }
        if (dueDate == null) {
            throw new IllegalArgumentException("Due date cannot be null");
        }
    }
}