package com.academia.domain.ports.out;

import com.academia.domain.model.aggregates.AcademicTerm;
import com.academia.domain.model.valueobjects.ids.AcademicTermId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;

import java.util.List;
import java.util.Optional;

public interface AcademicTermRepository {
    
    AcademicTerm save(AcademicTerm academicTerm);
    
    Optional<AcademicTerm> findById(AcademicTermId id);
    
    List<AcademicTerm> findByOrganizationId(OrganizationId organizationId);
    
    List<AcademicTerm> findCurrentTermsByOrganization(OrganizationId organizationId);
    
    Optional<AcademicTerm> findCurrentTermByOrganization(OrganizationId organizationId);
    
    List<AcademicTerm> findActiveTermsByOrganization(OrganizationId organizationId);
    
    void deleteById(AcademicTermId id);
    
    boolean existsByOrganizationIdAndName(OrganizationId organizationId, String name);
}