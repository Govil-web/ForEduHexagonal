package com.academia.application.handlers.queries;

import com.academia.domain.model.aggregates.Subject;
import com.academia.domain.model.valueobjects.ids.SubjectId;
import com.academia.domain.ports.out.SubjectRepository;
import com.academia.application.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetSubjectDetailsQueryHandler {
    
    private final SubjectRepository subjectRepository;
    
    public Subject handle(SubjectId subjectId) {
        return subjectRepository.findById(subjectId)
            .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + subjectId.getValue()));
    }
}