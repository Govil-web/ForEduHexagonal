package com.academia.infrastructure.persistence.mappers;

import com.academia.domain.model.aggregates.Subject;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.ids.SubjectId;
import com.academia.domain.model.valueobjects.academic.SubjectCode;
import com.academia.infrastructure.persistence.entities.SubjectEntity;
import org.springframework.stereotype.Component;

@Component
public class SubjectMapper {
    
    public SubjectEntity toEntity(Subject subject) {
        SubjectEntity entity = new SubjectEntity();
        entity.setId(subject.getId().getValue());
        entity.setOrganizationId(subject.getOrganizationId().getValue());
        entity.setName(subject.getName());
        entity.setSubjectCode(subject.getSubjectCode().getValue());
        entity.setDescription(subject.getDescription());
        entity.setCredits(subject.getCredits());
        entity.setIsActive(subject.isActive());
        return entity;
    }
    
    public Subject toDomain(SubjectEntity entity) {
        SubjectId subjectId = new SubjectId(entity.getId());
        OrganizationId organizationId = new OrganizationId(entity.getOrganizationId());
        SubjectCode subjectCode = new SubjectCode(entity.getSubjectCode());
        
        // Crear el agregado usando el constructor
        Subject subject = new Subject(
            subjectId,
            organizationId,
            entity.getName(),
            subjectCode,
            entity.getDescription(),
            entity.getCredits()
        );
        
        // Si no está activo, desactivarlo después de la creación
        if (!entity.getIsActive()) {
            subject.deactivate();
        }
        
        // Limpiar eventos de dominio ya que viene de la base de datos
        subject.clearDomainEvents();
        
        return subject;
    }
}