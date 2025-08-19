package com.academia.domain.ports.in.communication;

import com.academia.domain.ports.in.commands.CreateForumThreadCommand;
import com.academia.domain.ports.in.dtos.ForumThreadDetailsDTO;

/**
 * Use case for creating forum threads.
 */
public interface CreateForumThreadUseCase {
    
    /**
     * Creates a new forum thread with an initial post.
     * 
     * @param command the command containing thread details
     * @return the created thread details
     * @throws IllegalArgumentException if the command is invalid
     * @throws IllegalStateException if the author is not authorized for the course
     */
    ForumThreadDetailsDTO createForumThread(CreateForumThreadCommand command);
}