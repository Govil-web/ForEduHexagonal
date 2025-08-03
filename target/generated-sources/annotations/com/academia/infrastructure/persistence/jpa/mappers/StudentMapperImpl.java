package com.academia.infrastructure.persistence.jpa.mappers;

import com.academia.domain.model.entities.Student;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.OrganizationId;
import com.academia.infrastructure.persistence.jpa.entities.StudentProfileJpaEntity;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-03T17:31:09-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Amazon.com Inc.)"
)
@Component
public class StudentMapperImpl implements StudentMapper {

    @Override
    public StudentProfileJpaEntity toJpa(Student student) {
        if ( student == null ) {
            return null;
        }

        StudentProfileJpaEntity studentProfileJpaEntity = new StudentProfileJpaEntity();

        studentProfileJpaEntity.setUserId( studentAccountIdValue( student ) );
        studentProfileJpaEntity.setOrganizationId( studentOrganizationIdValue( student ) );
        studentProfileJpaEntity.setStudentIdNumber( student.getStudentIdNumber() );
        studentProfileJpaEntity.setEnrollmentDate( student.getEnrollmentDate() );
        studentProfileJpaEntity.setCurrentGradeLevel( student.getCurrentGradeLevel() );

        return studentProfileJpaEntity;
    }

    @Override
    public Student toDomain(StudentProfileJpaEntity jpaEntity) {
        if ( jpaEntity == null ) {
            return null;
        }

        AccountId accountId = null;
        OrganizationId organizationId = null;
        String studentIdNumber = null;
        LocalDate enrollmentDate = null;

        accountId = longToAccountId( jpaEntity.getUserId() );
        organizationId = longToOrganizationId( jpaEntity.getOrganizationId() );
        studentIdNumber = jpaEntity.getStudentIdNumber();
        enrollmentDate = jpaEntity.getEnrollmentDate();

        Student student = new Student( accountId, organizationId, studentIdNumber, enrollmentDate );

        return student;
    }

    private Long studentAccountIdValue(Student student) {
        if ( student == null ) {
            return null;
        }
        AccountId accountId = student.getAccountId();
        if ( accountId == null ) {
            return null;
        }
        Long value = accountId.getValue();
        if ( value == null ) {
            return null;
        }
        return value;
    }

    private Long studentOrganizationIdValue(Student student) {
        if ( student == null ) {
            return null;
        }
        OrganizationId organizationId = student.getOrganizationId();
        if ( organizationId == null ) {
            return null;
        }
        Long value = organizationId.getValue();
        if ( value == null ) {
            return null;
        }
        return value;
    }
}
