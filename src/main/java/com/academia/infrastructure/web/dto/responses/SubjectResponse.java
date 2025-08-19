package com.academia.infrastructure.web.dto.responses;

import com.academia.domain.model.aggregates.Subject;

public record SubjectResponse(
    Long id,
    Long organizationId,
    String name,
    String subjectCode,
    String description,
    int credits,
    boolean isActive
) {
    public static SubjectResponse fromDomain(Subject subject) {
        return new SubjectResponse(
            subject.getId().getValue(),
            subject.getOrganizationId().getValue(),
            subject.getName(),
            subject.getSubjectCode().getValue(),
            subject.getDescription(),
            subject.getCredits(),
            subject.isActive()
        );
    }
}