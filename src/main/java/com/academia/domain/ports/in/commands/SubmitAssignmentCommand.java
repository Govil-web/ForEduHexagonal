package com.academia.domain.ports.in.commands;

import com.academia.domain.model.valueobjects.ids.AssignmentId;
import com.academia.domain.model.valueobjects.ids.AccountId;

/**
 * Command to submit an assignment by a student.
 */
public record SubmitAssignmentCommand(
    AssignmentId assignmentId,
    AccountId studentId,
    String content
) {
    public SubmitAssignmentCommand {
        if (assignmentId == null) {
            throw new IllegalArgumentException("Assignment ID cannot be null");
        }
        if (studentId == null) {
            throw new IllegalArgumentException("Student ID cannot be null");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Submission content cannot be null or empty");
        }
    }
}