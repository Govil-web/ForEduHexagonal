package com.academia.infrastructure.web.requests;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record EnrollStudentInCourseRequest(
        @NotNull
        UUID studentAccountId,

        @NotNull
        UUID courseId
) {}
