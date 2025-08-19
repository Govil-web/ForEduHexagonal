package com.academia.application.handlers.queries;

import com.academia.domain.model.aggregates.Subject;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.ports.out.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetSubjectsByOrganizationQueryHandler {
    
    private final SubjectRepository subjectRepository;
    
    public List<Subject> handle(OrganizationId organizationId, boolean onlyActive) {
        if (onlyActive) {
            return subjectRepository.findActiveByOrganizationId(organizationId);
        }
        return subjectRepository.findByOrganizationId(organizationId);
    }
}