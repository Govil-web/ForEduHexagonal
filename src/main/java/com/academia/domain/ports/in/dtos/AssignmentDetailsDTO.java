package com.academia.domain.ports.in.dtos;

import com.academia.domain.model.enums.AssignmentType;

import java.time.LocalDateTime;

/**
 * DTO representing assignment details for API responses.
 */
public record AssignmentDetailsDTO(
    Long assignmentId,
    Long courseId,
    Long teacherId,
    String title,
    String description,
    AssignmentType type,
    LocalDateTime dueDate,
    boolean isPublished,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}