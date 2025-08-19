package com.academia.application.services;

import com.academia.domain.model.aggregates.AcademicTerm;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.ids.AcademicTermId;
import com.academia.domain.model.valueobjects.academic.TermDates;
import com.academia.domain.ports.out.AcademicTermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CreateAcademicTermService {
    
    private final AcademicTermRepository academicTermRepository;
    
    @Transactional
    public AcademicTerm createAcademicTerm(
            OrganizationId organizationId,
            String name,
            LocalDate startDate,
            LocalDate endDate,
            boolean isCurrentTerm
    ) {
        // Validaciones de negocio
        if (organizationId == null) {
            throw new IllegalArgumentException("Organization ID cannot be null");
        }
        
        // Si se va a crear como término actual, primero desmarcar otros términos actuales
        if (isCurrentTerm) {
            var currentTerms = academicTermRepository.findCurrentTermsByOrganization(organizationId);
            for (AcademicTerm currentTerm : currentTerms) {
                currentTerm.unmarkAsCurrent();
                academicTermRepository.save(currentTerm);
            }
        }
        
        // Crear value objects (null se asignará automáticamente por la BD)
        AcademicTermId termId = new AcademicTermId(null);
        TermDates termDates = new TermDates(startDate, endDate);
        
        // Crear el agregado AcademicTerm
        AcademicTerm academicTerm = new AcademicTerm(
            termId,
            organizationId,
            name,
            termDates
        );
        
        // Marcar como actual si corresponde
        if (isCurrentTerm) {
            academicTerm.markAsCurrent();
        }
        
        // Persistir
        return academicTermRepository.save(academicTerm);
    }
}