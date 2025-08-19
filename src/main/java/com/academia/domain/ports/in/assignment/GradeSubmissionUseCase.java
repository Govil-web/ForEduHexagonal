package com.academia.domain.ports.in.assignment;

import com.academia.domain.ports.in.commands.GradeSubmissionCommand;
import com.academia.domain.ports.in.dtos.SubmissionDetailsDTO;

/**
 * Use case for teachers to grade student submissions.
 */
public interface GradeSubmissionUseCase {
    
    /**
     * Grades a student's submission.
     * 
     * @param command the command containing grading details
     * @return the updated submission details
     * @throws IllegalArgumentException if the command is invalid
     * @throws IllegalStateException if the submission cannot be graded
     */
    SubmissionDetailsDTO gradeSubmission(GradeSubmissionCommand command);
}