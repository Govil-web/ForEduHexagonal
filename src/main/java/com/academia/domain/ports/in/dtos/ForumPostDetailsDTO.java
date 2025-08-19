package com.academia.domain.ports.in.dtos;

import java.time.LocalDateTime;

/**
 * DTO representing forum post details for API responses.
 */
public record ForumPostDetailsDTO(
    Long postId,
    Long threadId,
    Long authorId,
    Long parentPostId, // null for top-level posts
    String content,
    boolean isDeleted,
    boolean isReply,
    LocalDateTime createdAt,
    LocalDateTime editedAt,
    Long editedBy
) {}