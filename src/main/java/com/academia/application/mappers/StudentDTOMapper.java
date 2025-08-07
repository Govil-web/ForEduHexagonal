package com.academia.application.mappers;

import com.academia.domain.model.aggregates.UserAccount;
import com.academia.domain.model.entities.Student;
import com.academia.domain.ports.in.dtos.StudentDetailsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface StudentDTOMapper {

    StudentDTOMapper INSTANCE = Mappers.getMapper(StudentDTOMapper.class);

    @Mapping(source = "userAccount.user.id.value", target = "accountId")
    @Mapping(expression = "java(userAccount.getUser().getName().getFullName())", target = "fullName")
    @Mapping(source = "userAccount.user.email.value", target = "email")
    @Mapping(expression = "java(userAccount.getUser().getAge())", target = "age")
    @Mapping(source = "student.studentIdNumber", target = "studentIdNumber")
    @Mapping(source = "student.currentGradeLevel", target = "currentGradeLevel")
    @Mapping(source = "student.enrollmentDate", target = "enrollmentDate")
    @Mapping(source = "userAccount.user.accountStatus", target = "accountStatus")
    @Mapping(target = "guardians", ignore = true)
    StudentDetailsDTO toDTO(UserAccount userAccount, Student student);
}