package com.academia.infrastructure.web.mappers;

import com.academia.domain.ports.in.commands.EnrollStudentInCourseCommand;
import com.academia.infrastructure.web.requests.EnrollStudentInCourseRequest;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-04T18:55:31-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Amazon.com Inc.)"
)
@Component
public class EnrollStudentRequestMapperImpl implements EnrollStudentRequestMapper {

    @Override
    public EnrollStudentInCourseCommand toCommand(EnrollStudentInCourseRequest request) {
        if ( request == null ) {
            return null;
        }

        Long studentAccountId = null;
        Long courseId = null;

        studentAccountId = request.studentAccountId();
        courseId = request.courseId();

        EnrollStudentInCourseCommand enrollStudentInCourseCommand = new EnrollStudentInCourseCommand( studentAccountId, courseId );

        return enrollStudentInCourseCommand;
    }
}
