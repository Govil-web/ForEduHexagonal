package com.academia.infrastructure.persistence.adapters;

import com.academia.domain.model.entities.Student;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.domain.ports.out.StudentRepository;
import com.academia.infrastructure.persistence.jpa.mappers.StudentMapper; // Necesitaremos este mapper
import com.academia.infrastructure.persistence.jpa.repositories.SpringStudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaStudentRepositoryAdapter implements StudentRepository {

    private final SpringStudentProfileRepository jpaRepository;
    private final StudentMapper mapper;

    @Override
    public Student save(Student student) {
        var jpaEntity = mapper.toJpa(student);
        var savedEntity = jpaRepository.save(jpaEntity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Student> findById(AccountId accountId) {
        return jpaRepository.findById(accountId.getValue()).map(mapper::toDomain);
    }

    @Override
    public Optional<Student> findByAccountId(AccountId accountId) {
        return jpaRepository.findById(accountId.getValue()).map(mapper::toDomain);
    }

    @Override
    public boolean existsByStudentIdNumber(OrganizationId orgId, String studentIdNumber) {
        return jpaRepository.existsByOrganizationIdAndStudentIdNumber(orgId.getValue(), studentIdNumber);
    }
}
