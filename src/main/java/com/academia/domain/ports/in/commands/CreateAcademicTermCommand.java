package com.academia.domain.ports.in.commands;

import com.academia.domain.model.valueobjects.ids.OrganizationId;

import java.time.LocalDate;

public record CreateAcademicTermCommand(
    OrganizationId organizationId,
    String name,
    LocalDate startDate,
    LocalDate endDate,
    boolean isCurrentTerm
) {
}