package com.academia.domain.ports.in.commands;

import com.academia.domain.model.valueobjects.ids.OrganizationId;

public record CreateSubjectCommand(
    OrganizationId organizationId,
    String name,
    String subjectCode,
    String description,
    int credits
) {
}