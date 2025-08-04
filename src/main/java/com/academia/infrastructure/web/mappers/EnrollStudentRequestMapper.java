package com.academia.infrastructure.web.mappers;

import com.academia.domain.ports.in.commands.EnrollStudentInCourseCommand;
import com.academia.infrastructure.web.requests.EnrollStudentInCourseRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EnrollStudentRequestMapper {
    EnrollStudentInCourseCommand toCommand(EnrollStudentInCourseRequest request);
}
