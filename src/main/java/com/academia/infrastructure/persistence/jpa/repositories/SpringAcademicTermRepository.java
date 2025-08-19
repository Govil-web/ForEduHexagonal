package com.academia.infrastructure.persistence.jpa.repositories;

import com.academia.infrastructure.persistence.entities.AcademicTermEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface SpringAcademicTermRepository extends JpaRepository<AcademicTermEntity, UUID> {
    
    List<AcademicTermEntity> findByOrganizationId(UUID organizationId);
    
    List<AcademicTermEntity> findByOrganizationIdAndIsActiveTrue(UUID organizationId);
    
    List<AcademicTermEntity> findByOrganizationIdAndIsCurrentTermTrue(UUID organizationId);
    
    boolean existsByOrganizationIdAndName(UUID organizationId, String name);
    
    @Query("SELECT t FROM AcademicTermEntity t WHERE t.organizationId = :organizationId AND t.startDate <= :date AND t.endDate >= :date AND t.isActive = true")
    List<AcademicTermEntity> findActiveTermsOnDate(@Param("organizationId") UUID organizationId, @Param("date") LocalDate date);
    
    @Query("SELECT t FROM AcademicTermEntity t WHERE t.organizationId = :organizationId AND t.endDate < :date")
    List<AcademicTermEntity> findPastTerms(@Param("organizationId") UUID organizationId, @Param("date") LocalDate date);
    
    @Query("SELECT t FROM AcademicTermEntity t WHERE t.organizationId = :organizationId AND t.startDate > :date")
    List<AcademicTermEntity> findFutureTerms(@Param("organizationId") UUID organizationId, @Param("date") LocalDate date);

    /**
     * Nuevo método: Desmarca todos los términos actuales de una organización.
     * La anotación @Modifying es crucial para las operaciones de escritura.
     */
    @Modifying
    @Query("UPDATE AcademicTermEntity t SET t.isCurrentTerm = false WHERE t.organizationId = :organizationId AND t.isCurrentTerm = true")
    void unmarkAllCurrentTermsInOrganization(@Param("organizationId") UUID organizationId);
}