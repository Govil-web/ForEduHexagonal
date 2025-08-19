package com.academia.domain.ports.in.dtos;

import com.academia.domain.model.enums.AnnouncementPriority;
import com.academia.domain.model.enums.AnnouncementStatus;

import java.time.LocalDateTime;

/**
 * DTO representing announcement details for API responses.
 */
public record AnnouncementDetailsDTO(
    Long announcementId,
    Long organizationId,
    Long courseId, // null for organization-wide announcements
    Long authorId,
    String title,
    String content,
    AnnouncementPriority priority,
    AnnouncementStatus status,
    LocalDateTime expirationDate,
    LocalDateTime createdAt,
    LocalDateTime publishedAt,
    LocalDateTime updatedAt,
    boolean isVisible,
    boolean isExpired,
    Long daysUntilExpiration
) {}