package com.academia.infrastructure.persistence.mappers;

import com.academia.domain.model.aggregates.AcademicTerm;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.ids.AcademicTermId;
import com.academia.domain.model.valueobjects.academic.TermDates;
import com.academia.infrastructure.persistence.entities.AcademicTermEntity;
import org.springframework.stereotype.Component;

@Component
public class AcademicTermMapper {
    
    public AcademicTermEntity toEntity(AcademicTerm academicTerm) {
        AcademicTermEntity entity = new AcademicTermEntity();
        entity.setId(academicTerm.getId().getValue());
        entity.setOrganizationId(academicTerm.getOrganizationId().getValue());
        entity.setName(academicTerm.getName());
        entity.setStartDate(academicTerm.getTermDates().getStartDate());
        entity.setEndDate(academicTerm.getTermDates().getEndDate());
        entity.setIsActive(academicTerm.isActive());
        entity.setIsCurrentTerm(academicTerm.isCurrentTerm());
        return entity;
    }
    
    public AcademicTerm toDomain(AcademicTermEntity entity) {
        AcademicTermId termId = new AcademicTermId(entity.getId());
        OrganizationId organizationId = new OrganizationId(entity.getOrganizationId());
        TermDates termDates = new TermDates(entity.getStartDate(), entity.getEndDate());
        
        // Crear el agregado usando el constructor
        AcademicTerm academicTerm = new AcademicTerm(
            termId,
            organizationId,
            entity.getName(),
            termDates
        );
        
        // Aplicar estado después de la creación
        if (!entity.getIsActive()) {
            academicTerm.deactivate();
        }
        
        if (entity.getIsCurrentTerm()) {
            academicTerm.markAsCurrent();
        }
        
        // Limpiar eventos de dominio ya que viene de la base de datos
        academicTerm.clearDomainEvents();
        
        return academicTerm;
    }
}