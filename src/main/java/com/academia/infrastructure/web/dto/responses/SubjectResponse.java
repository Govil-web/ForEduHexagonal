package com.academia.infrastructure.web.dto.responses;

import com.academia.domain.model.aggregates.Subject;

import java.util.UUID;

public record SubjectResponse(
    UUID id,
    UUID organizationId,
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