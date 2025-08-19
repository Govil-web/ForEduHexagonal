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
public class DeactivateSubjectService {
    
    private final SubjectRepository subjectRepository;
    
    @Transactional
    public Subject deactivateSubject(SubjectId subjectId) {
        // Buscar el subject existente
        Subject subject = subjectRepository.findById(subjectId)
            .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + subjectId.getValue()));
        
        // Desactivar mediante método de dominio
        subject.deactivate();
        
        // Persistir cambios
        return subjectRepository.save(subject);
    }
}