package com.academia.domain.ports.in.communication;

import com.academia.domain.ports.in.commands.AddForumPostCommand;
import com.academia.domain.ports.in.dtos.ForumPostDetailsDTO;

/**
 * Use case for adding posts to forum threads.
 */
public interface AddForumPostUseCase {
    
    /**
     * Adds a new post to an existing forum thread.
     * 
     * @param command the command containing post details
     * @return the created post details
     * @throws IllegalArgumentException if the command is invalid
     * @throws IllegalStateException if the thread doesn't allow new posts
     */
    ForumPostDetailsDTO addForumPost(AddForumPostCommand command);
}