package com.academia.domain.ports.in.commands;

import com.academia.domain.model.valueobjects.ids.CourseId;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.enums.AssignmentType;

import java.time.LocalDateTime;

/**
 * Command to create a new assignment for a course.
 */
public record CreateAssignmentCommand(
    CourseId courseId,
    AccountId teacherId,
    String title,
    String description,
    AssignmentType type,
    LocalDateTime dueDate
) {
    public CreateAssignmentCommand {
        if (courseId == null) {
            throw new IllegalArgumentException("Course ID cannot be null");
        }
        if (teacherId == null) {
            throw new IllegalArgumentException("Teacher ID cannot be null");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Assignment title cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Assignment type cannot be null");
        }
        if (dueDate == null) {
            throw new IllegalArgumentException("Due date cannot be null");
        }
    }
}