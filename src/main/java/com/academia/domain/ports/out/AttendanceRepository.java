package com.academia.domain.ports.out;

import com.academia.domain.model.entities.AttendanceRecord;
import com.academia.domain.model.valueobjects.ids.AttendanceRecordId;
import com.academia.domain.model.valueobjects.ids.CourseId;
import com.academia.domain.model.valueobjects.ids.AccountId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for AttendanceRecord entity persistence operations.
 */
public interface AttendanceRepository {
    
    /**
     * Saves an attendance record to the repository.
     * 
     * @param attendanceRecord the attendance record to save
     * @return the saved attendance record
     */
    AttendanceRecord save(AttendanceRecord attendanceRecord);
    
    /**
     * Finds an attendance record by its ID.
     * 
     * @param attendanceRecordId the attendance record ID
     * @return an optional containing the attendance record if found
     */
    Optional<AttendanceRecord> findById(AttendanceRecordId attendanceRecordId);
    
    /**
     * Finds an attendance record for a specific student, course, and date.
     * 
     * @param courseId the course ID
     * @param studentId the student ID
     * @param date the attendance date
     * @return an optional containing the attendance record if found
     */
    Optional<AttendanceRecord> findByStudentCourseAndDate(CourseId courseId, AccountId studentId, LocalDate date);
    
    /**
     * Finds all attendance records for a specific course and date.
     * 
     * @param courseId the course ID
     * @param date the attendance date
     * @return list of attendance records for the course and date
     */
    List<AttendanceRecord> findByCourseAndDate(CourseId courseId, LocalDate date);
    
    /**
     * Finds all attendance records for a specific student.
     * 
     * @param studentId the student ID
     * @return list of attendance records for the student
     */
    List<AttendanceRecord> findByStudentId(AccountId studentId);
    
    /**
     * Finds attendance records for a student in a specific course.
     * 
     * @param courseId the course ID
     * @param studentId the student ID
     * @return list of attendance records for the student in the course
     */
    List<AttendanceRecord> findByStudentAndCourse(AccountId studentId, CourseId courseId);
    
    /**
     * Finds attendance records for a course within a date range.
     * 
     * @param courseId the course ID
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of attendance records within the date range
     */
    List<AttendanceRecord> findByCourseAndDateRange(CourseId courseId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Deletes an attendance record from the repository.
     * 
     * @param attendanceRecordId the attendance record ID to delete
     */
    void deleteById(AttendanceRecordId attendanceRecordId);
}