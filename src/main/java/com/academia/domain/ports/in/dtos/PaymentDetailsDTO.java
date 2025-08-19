package com.academia.domain.ports.in.dtos;

import com.academia.domain.model.enums.PaymentMethod;
import com.academia.domain.model.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO representing payment details for API responses.
 */
public record PaymentDetailsDTO(
    Long paymentId,
    Long feeId,
    Long payerId,
    BigDecimal amount,
    BigDecimal refundedAmount,
    BigDecimal netAmount,
    String currencyCode,
    PaymentMethod method,
    PaymentStatus status,
    String transactionId,
    String failureReason,
    LocalDateTime createdAt,
    LocalDateTime processedAt,
    LocalDateTime updatedAt
) {}