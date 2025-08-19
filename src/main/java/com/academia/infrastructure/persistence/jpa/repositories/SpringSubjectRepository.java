package com.academia.infrastructure.persistence.jpa.repositories;

import com.academia.infrastructure.persistence.entities.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SpringSubjectRepository extends JpaRepository<SubjectEntity, Long> {
    
    List<SubjectEntity> findByOrganizationId(Long organizationId);
    
    List<SubjectEntity> findByOrganizationIdAndIsActiveTrue(Long organizationId);
    
    boolean existsByOrganizationIdAndSubjectCode(Long organizationId, String subjectCode);
    
    @Query("SELECT s FROM SubjectEntity s WHERE s.organizationId = :organizationId AND s.name LIKE %:name%")
    List<SubjectEntity> findByOrganizationIdAndNameContaining(@Param("organizationId") Long organizationId, @Param("name") String name);
    
    @Query("SELECT COUNT(s) FROM SubjectEntity s WHERE s.organizationId = :organizationId AND s.isActive = true")
    long countActiveSubjectsByOrganization(@Param("organizationId") Long organizationId);
}