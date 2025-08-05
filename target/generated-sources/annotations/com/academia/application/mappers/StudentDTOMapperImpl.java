package com.academia.application.mappers;

import com.academia.domain.model.aggregates.UserAccount;
import com.academia.domain.model.entities.Student;
import com.academia.domain.model.entities.User;
import com.academia.domain.model.enums.AccountStatus;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.user.Email;
import com.academia.domain.ports.in.dtos.StudentDetailsDTO;
import java.time.LocalDate;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-04T23:25:08-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Amazon.com Inc.)"
)
@Component
public class StudentDTOMapperImpl implements StudentDTOMapper {

    @Override
    public StudentDetailsDTO toDTO(UserAccount userAccount, Student student) {
        if ( userAccount == null && student == null ) {
            return null;
        }

        Long accountId = null;
        String email = null;
        String accountStatus = null;
        if ( userAccount != null ) {
            accountId = userAccountUserIdValue( userAccount );
            email = userAccountUserEmailValue( userAccount );
            AccountStatus accountStatus1 = userAccountUserAccountStatus( userAccount );
            if ( accountStatus1 != null ) {
                accountStatus = accountStatus1.name();
            }
        }
        String studentIdNumber = null;
        String currentGradeLevel = null;
        LocalDate enrollmentDate = null;
        if ( student != null ) {
            studentIdNumber = student.getStudentIdNumber();
            currentGradeLevel = student.getCurrentGradeLevel();
            enrollmentDate = student.getEnrollmentDate();
        }

        String fullName = userAccount.getUser().getName().getFullName();
        int age = userAccount.getUser().getAge();
        List<StudentDetailsDTO.GuardianDTO> guardians = null;

        StudentDetailsDTO studentDetailsDTO = new StudentDetailsDTO( accountId, fullName, email, age, studentIdNumber, currentGradeLevel, enrollmentDate, accountStatus, guardians );

        return studentDetailsDTO;
    }

    private Long userAccountUserIdValue(UserAccount userAccount) {
        if ( userAccount == null ) {
            return null;
        }
        User user = userAccount.getUser();
        if ( user == null ) {
            return null;
        }
        AccountId id = user.getId();
        if ( id == null ) {
            return null;
        }
        Long value = id.getValue();
        if ( value == null ) {
            return null;
        }
        return value;
    }

    private String userAccountUserEmailValue(UserAccount userAccount) {
        if ( userAccount == null ) {
            return null;
        }
        User user = userAccount.getUser();
        if ( user == null ) {
            return null;
        }
        Email email = user.getEmail();
        if ( email == null ) {
            return null;
        }
        String value = email.value();
        if ( value == null ) {
            return null;
        }
        return value;
    }

    private AccountStatus userAccountUserAccountStatus(UserAccount userAccount) {
        if ( userAccount == null ) {
            return null;
        }
        User user = userAccount.getUser();
        if ( user == null ) {
            return null;
        }
        AccountStatus accountStatus = user.getAccountStatus();
        if ( accountStatus == null ) {
            return null;
        }
        return accountStatus;
    }
}
