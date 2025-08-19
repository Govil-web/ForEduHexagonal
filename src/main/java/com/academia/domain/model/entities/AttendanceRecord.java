package com.academia.domain.model.entities;

import com.academia.domain.model.valueobjects.ids.AttendanceRecordId;
import com.academia.domain.model.valueobjects.ids.CourseId;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.enums.AttendanceStatus;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a student's attendance record for a specific course session.
 * Tracks attendance status, timestamps, and any notes from instructors.
 */
@Getter
public class AttendanceRecord {
    private final AttendanceRecordId id;
    private final CourseId courseId;
    private final AccountId studentId;
    private final LocalDate attendanceDate;
    private AttendanceStatus status;
    private String notes;
    private final LocalDateTime recordedAt;
    private AccountId recordedBy;
    private LocalDateTime updatedAt;
    
    public AttendanceRecord(AttendanceRecordId id, CourseId courseId, AccountId studentId, 
                           LocalDate attendanceDate, AttendanceStatus status, AccountId recordedBy) {
        if (attendanceDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot record attendance for future dates");
        }
        
        this.id = id;
        this.courseId = courseId;
        this.studentId = studentId;
        this.attendanceDate = attendanceDate;
        this.status = status;
        this.recordedBy = recordedBy;
        this.recordedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Updates the attendance status.
     */
    public void updateStatus(AttendanceStatus newStatus, AccountId updatedBy) {
        this.status = newStatus;
        this.recordedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Adds or updates notes for this attendance record.
     */
    public void updateNotes(String notes, AccountId updatedBy) {
        this.notes = notes != null ? notes.trim() : "";
        this.recordedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Checks if the student was present on this date.
     */
    public boolean isPresent() {
        return status == AttendanceStatus.PRESENT;
    }
    
    /**
     * Checks if the student was absent on this date.
     */
    public boolean isAbsent() {
        return status == AttendanceStatus.ABSENT;
    }
    
    /**
     * Checks if the attendance record can be modified.
     * Records older than 7 days typically cannot be modified.
     */
    public boolean canBeModified() {
        return attendanceDate.isAfter(LocalDate.now().minusDays(7));
    }
}