package com.academia.domain.ports.out;

import com.academia.domain.model.aggregates.Fee;
import com.academia.domain.model.valueobjects.ids.FeeId;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.enums.FeeStatus;
import com.academia.domain.model.enums.FeeType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Fee aggregate persistence operations.
 */
public interface FeeRepository {
    
    /**
     * Saves a fee to the repository.
     * 
     * @param fee the fee to save
     * @return the saved fee
     */
    Fee save(Fee fee);
    
    /**
     * Finds a fee by its ID.
     * 
     * @param feeId the fee ID
     * @return an optional containing the fee if found
     */
    Optional<Fee> findById(FeeId feeId);
    
    /**
     * Finds all fees for a specific student.
     * 
     * @param studentId the student ID
     * @return list of fees for the student
     */
    List<Fee> findByStudentId(AccountId studentId);
    
    /**
     * Finds fees for a student with a specific status.
     * 
     * @param studentId the student ID
     * @param status the fee status
     * @return list of fees with the specified status
     */
    List<Fee> findByStudentIdAndStatus(AccountId studentId, FeeStatus status);
    
    /**
     * Finds all fees for an organization.
     * 
     * @param organizationId the organization ID
     * @return list of fees for the organization
     */
    List<Fee> findByOrganizationId(OrganizationId organizationId);
    
    /**
     * Finds fees by type for an organization.
     * 
     * @param organizationId the organization ID
     * @param feeType the fee type
     * @return list of fees of the specified type
     */
    List<Fee> findByOrganizationIdAndType(OrganizationId organizationId, FeeType feeType);
    
    /**
     * Finds fees that are due today.
     * 
     * @return list of fees due today
     */
    List<Fee> findFeesDueToday();
    
    /**
     * Finds fees that are overdue.
     * 
     * @return list of overdue fees
     */
    List<Fee> findOverdueFees();
    
    /**
     * Finds fees due within a specific date range.
     * 
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of fees due within the date range
     */
    List<Fee> findFeesDueBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Finds outstanding fees (requiring payment) for a student.
     * 
     * @param studentId the student ID
     * @return list of outstanding fees
     */
    List<Fee> findOutstandingFeesByStudent(AccountId studentId);
    
    /**
     * Deletes a fee from the repository.
     * 
     * @param feeId the fee ID to delete
     */
    void deleteById(FeeId feeId);
}