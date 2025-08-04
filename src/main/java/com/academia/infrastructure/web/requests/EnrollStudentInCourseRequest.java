package com.academia.infrastructure.web.requests;

import jakarta.validation.constraints.NotNull;

public record EnrollStudentInCourseRequest(
        @NotNull
        Long studentAccountId,

        @NotNull
        Long courseId
) {}
