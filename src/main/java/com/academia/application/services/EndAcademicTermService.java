package com.academia.application.services;

import com.academia.domain.model.aggregates.AcademicTerm;
import com.academia.domain.model.valueobjects.ids.AcademicTermId;
import com.academia.domain.ports.out.AcademicTermRepository;
import com.academia.application.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EndAcademicTermService {
    
    private final AcademicTermRepository academicTermRepository;
    
    @Transactional
    public AcademicTerm endAcademicTerm(AcademicTermId termId) {
        // Buscar el term existente
        AcademicTerm academicTerm = academicTermRepository.findById(termId)
            .orElseThrow(() -> new ResourceNotFoundException("Academic term not found with id: " + termId.getValue()));
        
        // Finalizar mediante método de dominio
        academicTerm.endTerm();
        
        // Persistir cambios
        return academicTermRepository.save(academicTerm);
    }
}