package com.academia.infrastructure.web.dto.responses;

import com.academia.domain.model.aggregates.AcademicTerm;

import java.time.LocalDate;

public record AcademicTermResponse(
    Long id,
    Long organizationId,
    String name,
    LocalDate startDate,
    LocalDate endDate,
    boolean isActive,
    boolean isCurrentTerm,
    boolean hasStarted,
    boolean hasEnded,
    boolean canEnroll
) {
    public static AcademicTermResponse fromDomain(AcademicTerm academicTerm) {
        return new AcademicTermResponse(
            academicTerm.getId().getValue(),
            academicTerm.getOrganizationId().getValue(),
            academicTerm.getName(),
            academicTerm.getTermDates().getStartDate(),
            academicTerm.getTermDates().getEndDate(),
            academicTerm.isActive(),
            academicTerm.isCurrentTerm(),
            academicTerm.hasStarted(),
            academicTerm.hasEnded(),
            academicTerm.canEnroll()
        );
    }
}