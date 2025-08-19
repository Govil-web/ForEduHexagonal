package com.academia.application.services;

import com.academia.domain.model.aggregates.AcademicTerm;
import com.academia.domain.model.valueobjects.ids.AcademicTermId;
import com.academia.domain.model.valueobjects.academic.TermDates;
import com.academia.domain.ports.out.AcademicTermRepository;
import com.academia.application.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UpdateAcademicTermDatesService {
    
    private final AcademicTermRepository academicTermRepository;
    
    @Transactional
    public AcademicTerm updateAcademicTermDates(
            AcademicTermId termId,
            LocalDate newStartDate,
            LocalDate newEndDate
    ) {
        // Buscar el term existente
        AcademicTerm academicTerm = academicTermRepository.findById(termId)
            .orElseThrow(() -> new ResourceNotFoundException("Academic term not found with id: " + termId.getValue()));
        
        // Crear nuevas fechas
        TermDates newTermDates = new TermDates(newStartDate, newEndDate);
        
        // Actualizar mediante método de dominio
        academicTerm.updateDates(newTermDates);
        
        // Persistir cambios
        return academicTermRepository.save(academicTerm);
    }
}