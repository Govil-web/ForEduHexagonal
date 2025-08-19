package com.academia.domain.ports.in.commands;

import com.academia.domain.model.valueobjects.ids.SubjectId;

public record UpdateSubjectCommand(
    SubjectId subjectId,
    String name,
    String description,
    int credits
) {
}