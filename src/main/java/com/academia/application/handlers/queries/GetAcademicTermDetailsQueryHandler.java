package com.academia.application.handlers.queries;

import com.academia.domain.model.aggregates.AcademicTerm;
import com.academia.domain.model.valueobjects.ids.AcademicTermId;
import com.academia.domain.ports.out.AcademicTermRepository;
import com.academia.application.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetAcademicTermDetailsQueryHandler {
    
    private final AcademicTermRepository academicTermRepository;
    
    public AcademicTerm handle(AcademicTermId termId) {
        return academicTermRepository.findById(termId)
            .orElseThrow(() -> new ResourceNotFoundException("Academic term not found with id: " + termId.getValue()));
    }
}