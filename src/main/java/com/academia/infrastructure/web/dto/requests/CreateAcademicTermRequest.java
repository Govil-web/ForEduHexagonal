package com.academia.infrastructure.web.dto.requests;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateAcademicTermRequest(
    @NotNull(message = "Organization ID is required")
    Long organizationId,
    
    @NotBlank(message = "Term name is required")
    @Size(max = 255, message = "Term name must not exceed 255 characters")
    String name,
    
    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be today or in the future")
    LocalDate startDate,
    
    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    LocalDate endDate,
    
    boolean isCurrentTerm
) {
    public CreateAcademicTermRequest {
        if (startDate != null && endDate != null && !startDate.isBefore(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
    }
}