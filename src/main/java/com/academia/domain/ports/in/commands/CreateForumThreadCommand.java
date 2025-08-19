package com.academia.domain.ports.in.commands;

import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.CourseId;

/**
 * Command to create a new forum thread.
 */
public record CreateForumThreadCommand(
    CourseId courseId,
    AccountId authorId,
    String title,
    String description,
    String initialPostContent
) {
    public CreateForumThreadCommand {
        if (courseId == null) {
            throw new IllegalArgumentException("Course ID cannot be null");
        }
        if (authorId == null) {
            throw new IllegalArgumentException("Author ID cannot be null");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Thread title cannot be null or empty");
        }
        if (initialPostContent == null || initialPostContent.trim().isEmpty()) {
            throw new IllegalArgumentException("Initial post content cannot be null or empty");
        }
    }
}