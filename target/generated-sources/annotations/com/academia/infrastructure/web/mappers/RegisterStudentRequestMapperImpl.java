package com.academia.infrastructure.web.mappers;

import com.academia.domain.ports.in.commands.RegisterNewStudentCommand;
import com.academia.infrastructure.web.requests.RegisterStudentRequest;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-03T17:31:09-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Amazon.com Inc.)"
)
@Component
public class RegisterStudentRequestMapperImpl implements RegisterStudentRequestMapper {

    @Override
    public RegisterNewStudentCommand toCommand(RegisterStudentRequest request) {
        if ( request == null ) {
            return null;
        }

        Long organizationId = null;
        String firstName = null;
        String lastName = null;
        String email = null;
        LocalDate birthDate = null;
        String studentIdNumber = null;
        LocalDate enrollmentDate = null;
        String initialGradeLevel = null;

        organizationId = request.organizationId();
        firstName = request.firstName();
        lastName = request.lastName();
        email = request.email();
        birthDate = request.birthDate();
        studentIdNumber = request.studentIdNumber();
        enrollmentDate = request.enrollmentDate();
        initialGradeLevel = request.initialGradeLevel();

        RegisterNewStudentCommand registerNewStudentCommand = new RegisterNewStudentCommand( organizationId, firstName, lastName, email, birthDate, studentIdNumber, enrollmentDate, initialGradeLevel );

        return registerNewStudentCommand;
    }
}
