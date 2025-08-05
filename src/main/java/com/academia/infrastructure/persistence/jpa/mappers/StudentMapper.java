package com.academia.infrastructure.persistence.jpa.mappers;

import com.academia.domain.model.entities.Student;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.infrastructure.persistence.jpa.entities.StudentProfileJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    // --- De Dominio a Entidad JPA ---
    @Mapping(source = "accountId.value", target = "userId")
    @Mapping(source = "organizationId.value", target = "organizationId")
    @Mapping(source = "studentIdNumber", target = "studentIdNumber")
    @Mapping(source = "enrollmentDate", target = "enrollmentDate")
    @Mapping(source = "currentGradeLevel", target = "currentGradeLevel")
    // REMOVIDO: observations no existe en StudentProfileJpaEntity
    StudentProfileJpaEntity toJpa(Student student);

    // --- De Entidad JPA a Dominio ---
    // NOTA: Student tiene campos final, usamos método custom
    default Student toDomain(StudentProfileJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        AccountId accountId = jpaEntity.getUserId() != null ? new AccountId(jpaEntity.getUserId()) : null;
        OrganizationId organizationId = jpaEntity.getOrganizationId() != null ? new OrganizationId(jpaEntity.getOrganizationId()) : null;

        // Crear Student usando el constructor existente
        Student student = new Student(
                accountId,
                organizationId,
                jpaEntity.getStudentIdNumber(),
                jpaEntity.getEnrollmentDate()
        );

        // Setear currentGradeLevel usando el método del dominio
        if (jpaEntity.getCurrentGradeLevel() != null) {
            student.changeGradeLevel(jpaEntity.getCurrentGradeLevel());
        }

        return student;
    }
}