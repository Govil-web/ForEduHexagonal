package com.academia.domain.ports.out;

import com.academia.domain.model.aggregates.Announcement;
import com.academia.domain.model.valueobjects.ids.AnnouncementId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.ids.CourseId;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.enums.AnnouncementStatus;
import com.academia.domain.model.enums.AnnouncementPriority;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Announcement aggregate persistence operations.
 */
public interface AnnouncementRepository {
    
    /**
     * Saves an announcement to the repository.
     * 
     * @param announcement the announcement to save
     * @return the saved announcement
     */
    Announcement save(Announcement announcement);
    
    /**
     * Finds an announcement by its ID.
     * 
     * @param announcementId the announcement ID
     * @return an optional containing the announcement if found
     */
    Optional<Announcement> findById(AnnouncementId announcementId);
    
    /**
     * Finds all announcements for an organization.
     * 
     * @param organizationId the organization ID
     * @return list of announcements for the organization
     */
    List<Announcement> findByOrganizationId(OrganizationId organizationId);
    
    /**
     * Finds announcements for a specific course.
     * 
     * @param courseId the course ID
     * @return list of announcements for the course
     */
    List<Announcement> findByCourseId(CourseId courseId);
    
    /**
     * Finds organization-wide announcements (not course-specific).
     * 
     * @param organizationId the organization ID
     * @return list of organization-wide announcements
     */
    List<Announcement> findOrganizationWideAnnouncements(OrganizationId organizationId);
    
    /**
     * Finds visible announcements for an organization.
     * 
     * @param organizationId the organization ID
     * @return list of visible announcements
     */
    List<Announcement> findVisibleByOrganizationId(OrganizationId organizationId);
    
    /**
     * Finds visible announcements for a course.
     * 
     * @param courseId the course ID
     * @return list of visible announcements for the course
     */
    List<Announcement> findVisibleByCourseId(CourseId courseId);
    
    /**
     * Finds announcements by author.
     * 
     * @param authorId the author ID
     * @return list of announcements by the author
     */
    List<Announcement> findByAuthorId(AccountId authorId);
    
    /**
     * Finds announcements with a specific status.
     * 
     * @param status the announcement status
     * @return list of announcements with the specified status
     */
    List<Announcement> findByStatus(AnnouncementStatus status);
    
    /**
     * Finds announcements with a specific priority.
     * 
     * @param priority the announcement priority
     * @return list of announcements with the specified priority
     */
    List<Announcement> findByPriority(AnnouncementPriority priority);
    
    /**
     * Finds announcements that are expiring soon.
     * 
     * @param expirationThreshold the threshold date
     * @return list of announcements expiring before the threshold
     */
    List<Announcement> findExpiringSoon(LocalDateTime expirationThreshold);
    
    /**
     * Finds announcements that have expired.
     * 
     * @return list of expired announcements
     */
    List<Announcement> findExpiredAnnouncements();
    
    /**
     * Deletes an announcement from the repository.
     * 
     * @param announcementId the announcement ID to delete
     */
    void deleteById(AnnouncementId announcementId);
}