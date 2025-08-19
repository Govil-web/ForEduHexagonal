package com.academia.domain.ports.in.commands;

import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.ids.CourseId;
import com.academia.domain.model.enums.AnnouncementPriority;

import java.time.LocalDateTime;

/**
 * Command to create a new announcement.
 */
public record CreateAnnouncementCommand(
    OrganizationId organizationId,
    CourseId courseId, // null for organization-wide announcements
    AccountId authorId,
    String title,
    String content,
    AnnouncementPriority priority,
    LocalDateTime expirationDate
) {
    public CreateAnnouncementCommand {
        if (organizationId == null) {
            throw new IllegalArgumentException("Organization ID cannot be null");
        }
        if (authorId == null) {
            throw new IllegalArgumentException("Author ID cannot be null");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Announcement title cannot be null or empty");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Announcement content cannot be null or empty");
        }
    }
}