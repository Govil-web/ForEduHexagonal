package com.academia.infrastructure.persistence.jpa.repositories;

import com.academia.infrastructure.persistence.entities.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringSubjectRepository extends JpaRepository<SubjectEntity, UUID> {
    
    List<SubjectEntity> findByOrganizationId(UUID organizationId);
    
    List<SubjectEntity> findByOrganizationIdAndIsActiveTrue(UUID organizationId);
    
    boolean existsByOrganizationIdAndSubjectCode(UUID organizationId, String subjectCode);
    
    @Query("SELECT s FROM SubjectEntity s WHERE s.organizationId = :organizationId AND s.name LIKE %:name%")
    List<SubjectEntity> findByOrganizationIdAndNameContaining(@Param("organizationId") UUID organizationId, @Param("name") String name);
    
    @Query("SELECT COUNT(s) FROM SubjectEntity s WHERE s.organizationId = :organizationId AND s.isActive = true")
    long countActiveSubjectsByOrganization(@Param("organizationId") UUID organizationId);
}