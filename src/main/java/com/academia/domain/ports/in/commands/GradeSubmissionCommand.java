package com.academia.domain.ports.in.commands;

import com.academia.domain.model.valueobjects.ids.SubmissionId;
import com.academia.domain.model.valueobjects.ids.AccountId;

/**
 * Command to grade a student's submission.
 */
public record GradeSubmissionCommand(
    SubmissionId submissionId,
    AccountId graderId,
    double gradeValue,
    String feedback
) {
    public GradeSubmissionCommand {
        if (submissionId == null) {
            throw new IllegalArgumentException("Submission ID cannot be null");
        }
        if (graderId == null) {
            throw new IllegalArgumentException("Grader ID cannot be null");
        }
        if (gradeValue < 0.0 || gradeValue > 100.0) {
            throw new IllegalArgumentException("Grade value must be between 0 and 100");
        }
    }
}