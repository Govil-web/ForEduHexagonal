package com.academia.domain.ports.in.finance;

import com.academia.domain.ports.in.commands.CreateFeeCommand;
import com.academia.domain.ports.in.dtos.FeeDetailsDTO;

/**
 * Use case for creating fees for students.
 */
public interface CreateFeeUseCase {
    
    /**
     * Creates a new fee for the specified student.
     * 
     * @param command the command containing fee details
     * @return the created fee details
     * @throws IllegalArgumentException if the command is invalid
     * @throws IllegalStateException if the student is not found
     */
    FeeDetailsDTO createFee(CreateFeeCommand command);
}