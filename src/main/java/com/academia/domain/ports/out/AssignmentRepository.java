package com.academia.domain.ports.out;

import com.academia.domain.model.aggregates.Assignment;
import com.academia.domain.model.valueobjects.ids.AssignmentId;
import com.academia.domain.model.valueobjects.ids.CourseId;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Assignment aggregate persistence operations.
 */
public interface AssignmentRepository {
    
    /**
     * Saves an assignment to the repository.
     * 
     * @param assignment the assignment to save
     * @return the saved assignment
     */
    Assignment save(Assignment assignment);
    
    /**
     * Finds an assignment by its ID.
     * 
     * @param assignmentId the assignment ID
     * @return an optional containing the assignment if found
     */
    Optional<Assignment> findById(AssignmentId assignmentId);
    
    /**
     * Finds all assignments for a specific course.
     * 
     * @param courseId the course ID
     * @return list of assignments for the course
     */
    List<Assignment> findByCourseId(CourseId courseId);
    
    /**
     * Finds all published assignments for a specific course.
     * 
     * @param courseId the course ID
     * @return list of published assignments for the course
     */
    List<Assignment> findPublishedByCourseId(CourseId courseId);
    
    /**
     * Finds assignments that are overdue and published.
     * 
     * @return list of overdue assignments
     */
    List<Assignment> findOverdueAssignments();
    
    /**
     * Deletes an assignment from the repository.
     * 
     * @param assignmentId the assignment ID to delete
     */
    void deleteById(AssignmentId assignmentId);
}