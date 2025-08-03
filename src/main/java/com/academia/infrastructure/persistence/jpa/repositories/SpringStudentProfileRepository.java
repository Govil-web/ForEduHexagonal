package com.academia.infrastructure.persistence.jpa.repositories;
import com.academia.infrastructure.persistence.jpa.entities.StudentProfileJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SpringStudentProfileRepository extends JpaRepository<StudentProfileJpaEntity, Long> {
    boolean existsByOrganizationIdAndStudentIdNumber(Long organizationId, String studentIdNumber);
}
