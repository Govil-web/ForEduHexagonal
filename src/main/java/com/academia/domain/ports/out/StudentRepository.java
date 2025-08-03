package com.academia.domain.ports.out;
import com.academia.domain.model.entities.Student;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;

import java.util.Optional;

public interface StudentRepository {
    Student save(Student student);
    Optional<Student> findById(AccountId accountId);
    boolean existsByStudentIdNumber(OrganizationId orgId, String studentIdNumber);
    Optional<Student> findByAccountId(AccountId accountId);
}