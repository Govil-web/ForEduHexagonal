package com.academia.application.services;

import com.academia.domain.model.aggregates.AcademicTerm;
import com.academia.domain.model.valueobjects.ids.AcademicTermId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.ports.out.AcademicTermRepository;
import com.academia.application.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SetCurrentTermService {
    
    private final AcademicTermRepository academicTermRepository;
    
    @Transactional
    public AcademicTerm setCurrentTerm(AcademicTermId termId, OrganizationId organizationId) {
        // Buscar el term que será el actual
        AcademicTerm newCurrentTerm = academicTermRepository.findById(termId)
            .orElseThrow(() -> new ResourceNotFoundException("Academic term not found with id: " + termId.getValue()));
        
        // Buscar todos los términos de la organización que estén marcados como actuales
        List<AcademicTerm> currentTerms = academicTermRepository.findCurrentTermsByOrganization(organizationId);
        
        // Desmarcar todos los términos actuales
        for (AcademicTerm currentTerm : currentTerms) {
            currentTerm.unmarkAsCurrent();
            academicTermRepository.save(currentTerm);
        }
        
        // Marcar el nuevo término como actual
        newCurrentTerm.markAsCurrent();
        
        // Persistir cambios
        return academicTermRepository.save(newCurrentTerm);
    }
}