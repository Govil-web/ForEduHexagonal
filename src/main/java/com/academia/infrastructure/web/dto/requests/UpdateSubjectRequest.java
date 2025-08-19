package com.academia.infrastructure.web.dto.requests;

import jakarta.validation.constraints.*;

public record UpdateSubjectRequest(
    @NotBlank(message = "Subject name is required")
    @Size(max = 255, message = "Subject name must not exceed 255 characters")
    String name,
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    String description,
    
    @NotNull(message = "Credits is required")
    @Min(value = 1, message = "Credits must be at least 1")
    @Max(value = 20, message = "Credits must not exceed 20")
    Integer credits
) {
}