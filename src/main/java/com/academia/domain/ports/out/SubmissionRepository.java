package com.academia.domain.ports.out;

import com.academia.domain.model.aggregates.Submission;
import com.academia.domain.model.valueobjects.ids.SubmissionId;
import com.academia.domain.model.valueobjects.ids.AssignmentId;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.enums.SubmissionStatus;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Submission aggregate persistence operations.
 */
public interface SubmissionRepository {
    
    /**
     * Saves a submission to the repository.
     * 
     * @param submission the submission to save
     * @return the saved submission
     */
    Submission save(Submission submission);
    
    /**
     * Finds a submission by its ID.
     * 
     * @param submissionId the submission ID
     * @return an optional containing the submission if found
     */
    Optional<Submission> findById(SubmissionId submissionId);
    
    /**
     * Finds a submission by assignment and student.
     * 
     * @param assignmentId the assignment ID
     * @param studentId the student ID
     * @return an optional containing the submission if found
     */
    Optional<Submission> findByAssignmentAndStudent(AssignmentId assignmentId, AccountId studentId);
    
    /**
     * Finds all submissions for a specific assignment.
     * 
     * @param assignmentId the assignment ID
     * @return list of submissions for the assignment
     */
    List<Submission> findByAssignmentId(AssignmentId assignmentId);
    
    /**
     * Finds all submissions by a specific student.
     * 
     * @param studentId the student ID
     * @return list of submissions by the student
     */
    List<Submission> findByStudentId(AccountId studentId);
    
    /**
     * Finds submissions with a specific status.
     * 
     * @param status the submission status
     * @return list of submissions with the specified status
     */
    List<Submission> findByStatus(SubmissionStatus status);
    
    /**
     * Finds submissions ready for grading (submitted but not graded).
     * 
     * @param assignmentId the assignment ID
     * @return list of submissions ready for grading
     */
    List<Submission> findSubmissionsForGrading(AssignmentId assignmentId);
    
    /**
     * Deletes a submission from the repository.
     * 
     * @param submissionId the submission ID to delete
     */
    void deleteById(SubmissionId submissionId);
}