package com.academia.domain.ports.in.dtos;

import com.academia.domain.model.enums.ForumThreadStatus;

import java.time.LocalDateTime;

/**
 * DTO representing forum thread details for API responses.
 */
public record ForumThreadDetailsDTO(
    Long threadId,
    Long courseId,
    Long authorId,
    String title,
    String description,
    ForumThreadStatus status,
    int postCount,
    int replyCount,
    LocalDateTime lastActivityAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    boolean allowsNewPosts,
    boolean isPinned
) {}