package com.academia.domain.model.entities;
import com.academia.domain.model.enums.EnrollmentStatus;
import com.academia.domain.model.valueobjects.academic.Grade;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.CourseId;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class Enrollment {
    private final Long id;
    private final AccountId studentId;
    private final CourseId courseId;
    private EnrollmentStatus status;
    private Grade finalGrade;
    private final LocalDateTime enrollmentDate;

    public Enrollment(Long id, AccountId studentId, CourseId courseId) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.status = EnrollmentStatus.ACTIVE;
        this.enrollmentDate = LocalDateTime.now();
    }

    public void withdraw() {
        if (this.status == EnrollmentStatus.COMPLETED) {
            throw new IllegalStateException("No se puede retirar de un curso ya completado.");
        }
        this.status = EnrollmentStatus.WITHDRAWN;
    }

    public void complete(Grade finalGrade) {
        if (this.status != EnrollmentStatus.ACTIVE) {
            throw new IllegalStateException("Solo se pueden completar inscripciones activas.");
        }
        this.finalGrade = finalGrade;
        this.status = EnrollmentStatus.COMPLETED;
    }
}