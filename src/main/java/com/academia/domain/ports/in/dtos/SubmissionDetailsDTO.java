package com.academia.domain.ports.in.dtos;

import com.academia.domain.model.enums.SubmissionStatus;

import java.time.LocalDateTime;

/**
 * DTO representing submission details for API responses.
 */
public record SubmissionDetailsDTO(
    Long submissionId,
    Long assignmentId,
    Long studentId,
    String content,
    SubmissionStatus status,
    Double grade,
    String feedback,
    Long graderId,
    LocalDateTime createdAt,
    LocalDateTime submittedAt,
    LocalDateTime gradedAt,
    LocalDateTime updatedAt
) {}