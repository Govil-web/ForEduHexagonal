package com.academia.domain.ports.in.commands;

import com.academia.domain.model.valueobjects.ids.ForumThreadId;
import com.academia.domain.model.valueobjects.ids.ForumPostId;
import com.academia.domain.model.valueobjects.ids.AccountId;

/**
 * Command to add a new post to a forum thread.
 */
public record AddForumPostCommand(
    ForumThreadId threadId,
    AccountId authorId,
    ForumPostId parentPostId, // null for top-level posts
    String content
) {
    public AddForumPostCommand {
        if (threadId == null) {
            throw new IllegalArgumentException("Thread ID cannot be null");
        }
        if (authorId == null) {
            throw new IllegalArgumentException("Author ID cannot be null");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Post content cannot be null or empty");
        }
    }
}