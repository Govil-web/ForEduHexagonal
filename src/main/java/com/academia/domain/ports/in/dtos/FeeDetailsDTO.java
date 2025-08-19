package com.academia.domain.ports.in.dtos;

import com.academia.domain.model.enums.FeeType;
import com.academia.domain.model.enums.FeeStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO representing fee details for API responses.
 */
public record FeeDetailsDTO(
    Long feeId,
    Long studentId,
    Long organizationId,
    FeeType feeType,
    BigDecimal originalAmount,
    BigDecimal paidAmount,
    BigDecimal remainingBalance,
    String currencyCode,
    FeeStatus status,
    LocalDate dueDate,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    boolean isOverdue,
    int daysPastDue
) {}