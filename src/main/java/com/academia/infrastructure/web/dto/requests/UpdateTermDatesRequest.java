package com.academia.infrastructure.web.dto.requests;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UpdateTermDatesRequest(
    @NotNull(message = "Start date is required")
    LocalDate startDate,
    
    @NotNull(message = "End date is required")
    LocalDate endDate
) {
    public UpdateTermDatesRequest {
        if (startDate != null && endDate != null && !startDate.isBefore(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
    }
}