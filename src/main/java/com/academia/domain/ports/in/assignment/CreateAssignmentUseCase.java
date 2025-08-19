package com.academia.domain.ports.in.assignment;

import com.academia.domain.ports.in.commands.CreateAssignmentCommand;
import com.academia.domain.ports.in.dtos.AssignmentDetailsDTO;

/**
 * Use case for creating a new assignment in a course.
 */
public interface CreateAssignmentUseCase {
    
    /**
     * Creates a new assignment for the specified course.
     * 
     * @param command the command containing assignment details
     * @return the created assignment details
     * @throws IllegalArgumentException if the command is invalid
     * @throws IllegalStateException if the teacher is not authorized for the course
     */
    AssignmentDetailsDTO createAssignment(CreateAssignmentCommand command);
}