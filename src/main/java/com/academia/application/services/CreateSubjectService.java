package com.academia.application.services;

import com.academia.domain.model.aggregates.Subject;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.ids.SubjectId;
import com.academia.domain.model.valueobjects.academic.SubjectCode;
import com.academia.domain.ports.out.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateSubjectService {
    
    private final SubjectRepository subjectRepository;
    
    @Transactional
    public Subject createSubject(
            OrganizationId organizationId,
            String name,
            String subjectCodeValue,
            String description,
            int credits
    ) {
        // Validaciones de negocio
        if (organizationId == null) {
            throw new IllegalArgumentException("Organization ID cannot be null");
        }
        
        // Crear value objects (null se asignará automáticamente por la BD)
        SubjectId subjectId = new SubjectId(null);
        SubjectCode subjectCode = new SubjectCode(subjectCodeValue);
        
        // Crear el agregado Subject
        Subject subject = new Subject(
            subjectId,
            organizationId,
            name,
            subjectCode,
            description,
            credits
        );
        
        // Persistir
        return subjectRepository.save(subject);
    }
}