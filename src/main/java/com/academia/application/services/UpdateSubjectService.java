package com.academia.application.services;

import com.academia.domain.model.aggregates.Subject;
import com.academia.domain.model.valueobjects.ids.SubjectId;
import com.academia.domain.ports.out.SubjectRepository;
import com.academia.application.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateSubjectService {
    
    private final SubjectRepository subjectRepository;
    
    @Transactional
    public Subject updateSubject(
            SubjectId subjectId,
            String name,
            String description,
            int credits
    ) {
        // Buscar el subject existente
        Subject subject = subjectRepository.findById(subjectId)
            .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + subjectId.getValue()));
        
        // Actualizar mediante método de dominio
        subject.updateDetails(name, description, credits);
        
        // Persistir cambios
        return subjectRepository.save(subject);
    }
}