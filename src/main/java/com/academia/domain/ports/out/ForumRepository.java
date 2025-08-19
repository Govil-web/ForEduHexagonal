package com.academia.domain.ports.out;

import com.academia.domain.model.aggregates.ForumThread;
import com.academia.domain.model.valueobjects.ids.ForumThreadId;
import com.academia.domain.model.valueobjects.ids.CourseId;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.enums.ForumThreadStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ForumThread aggregate persistence operations.
 */
public interface ForumRepository {
    
    /**
     * Saves a forum thread to the repository.
     * 
     * @param forumThread the forum thread to save
     * @return the saved forum thread
     */
    ForumThread save(ForumThread forumThread);
    
    /**
     * Finds a forum thread by its ID.
     * 
     * @param threadId the thread ID
     * @return an optional containing the thread if found
     */
    Optional<ForumThread> findById(ForumThreadId threadId);
    
    /**
     * Finds all forum threads for a specific course.
     * 
     * @param courseId the course ID
     * @return list of forum threads for the course
     */
    List<ForumThread> findByCourseId(CourseId courseId);
    
    /**
     * Finds active forum threads for a course.
     * 
     * @param courseId the course ID
     * @return list of active forum threads
     */
    List<ForumThread> findActiveByCourseId(CourseId courseId);
    
    /**
     * Finds forum threads by author.
     * 
     * @param authorId the author ID
     * @return list of forum threads by the author
     */
    List<ForumThread> findByAuthorId(AccountId authorId);
    
    /**
     * Finds forum threads with a specific status.
     * 
     * @param status the thread status
     * @return list of threads with the specified status
     */
    List<ForumThread> findByStatus(ForumThreadStatus status);
    
    /**
     * Finds pinned forum threads for a course.
     * 
     * @param courseId the course ID
     * @return list of pinned threads
     */
    List<ForumThread> findPinnedByCourseId(CourseId courseId);
    
    /**
     * Finds forum threads with recent activity.
     * 
     * @param courseId the course ID
     * @param since the date threshold for recent activity
     * @return list of threads with recent activity
     */
    List<ForumThread> findWithRecentActivity(CourseId courseId, LocalDateTime since);
    
    /**
     * Finds forum threads ordered by last activity.
     * 
     * @param courseId the course ID
     * @param limit maximum number of threads to return
     * @return list of threads ordered by last activity (most recent first)
     */
    List<ForumThread> findByLastActivity(CourseId courseId, int limit);
    
    /**
     * Deletes a forum thread from the repository.
     * 
     * @param threadId the thread ID to delete
     */
    void deleteById(ForumThreadId threadId);
}