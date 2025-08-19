package com.academia.domain.ports.in.assignment;

import com.academia.domain.ports.in.commands.SubmitAssignmentCommand;
import com.academia.domain.ports.in.dtos.SubmissionDetailsDTO;

/**
 * Use case for students to submit assignments.
 */
public interface SubmitAssignmentUseCase {
    
    /**
     * Submits an assignment for grading.
     * 
     * @param command the command containing submission details
     * @return the submission details
     * @throws IllegalArgumentException if the command is invalid
     * @throws IllegalStateException if the assignment cannot be submitted
     */
    SubmissionDetailsDTO submitAssignment(SubmitAssignmentCommand command);
}