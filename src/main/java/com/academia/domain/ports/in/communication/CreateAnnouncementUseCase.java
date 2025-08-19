package com.academia.domain.ports.in.communication;

import com.academia.domain.ports.in.commands.CreateAnnouncementCommand;
import com.academia.domain.ports.in.dtos.AnnouncementDetailsDTO;

/**
 * Use case for creating announcements.
 */
public interface CreateAnnouncementUseCase {
    
    /**
     * Creates a new announcement.
     * 
     * @param command the command containing announcement details
     * @return the created announcement details
     * @throws IllegalArgumentException if the command is invalid
     * @throws IllegalStateException if the author is not authorized
     */
    AnnouncementDetailsDTO createAnnouncement(CreateAnnouncementCommand command);
}