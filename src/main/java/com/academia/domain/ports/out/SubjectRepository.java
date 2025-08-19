package com.academia.domain.ports.out;

import com.academia.domain.model.aggregates.Subject;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.model.valueobjects.ids.SubjectId;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository {

    Subject save(Subject subject);

    Optional<Subject> findById(SubjectId id);
    
    List<Subject> findByOrganizationId(OrganizationId organizationId);
    
    List<Subject> findActiveByOrganizationId(OrganizationId organizationId);
    
    void deleteById(SubjectId id);
    
    boolean existsByOrganizationIdAndSubjectCode(OrganizationId organizationId, String subjectCode);

}
