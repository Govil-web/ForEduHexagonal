package com.academia.domain.model.aggregates;
import com.academia.domain.model.entities.Enrollment;
import com.academia.domain.model.entities.Staff;
import com.academia.domain.model.entities.Student;
import com.academia.domain.model.entities.Subject;
import com.academia.domain.model.entities.AcademicTerm;
import com.academia.domain.model.events.CourseEvents;
import com.academia.domain.model.events.DomainEvent;
import com.academia.domain.model.services.EnrollmentEligibilityChecker;
import com.academia.domain.model.valueobjects.ids.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class Course {
    private final CourseId id;
    private final Subject subject;
    private final AcademicTerm term;
    private AccountId teacherId;
    private Set<Enrollment> enrollments;
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    public Course(CourseId id, Subject subject, AcademicTerm term) {
        this.id = id;
        this.subject = subject;
        this.term = term;
        this.enrollments = new HashSet<>();
    }

    public void assignTeacher(Staff teacher) {
        // Lógica de negocio: validar si el staff tiene rol de profesor
        this.teacherId = teacher.getAccountId();
        domainEvents.add(new CourseEvents.TeacherAssignedToCourse(this.id, teacher.getAccountId()));
    }

    public Enrollment enrollStudent(Student student, EnrollmentEligibilityChecker eligibilityChecker) {
        // Usar un Domain Service para lógica que cruza agregados
        if (!eligibilityChecker.isStudentEligible(student, this)) {
            throw new IllegalStateException("El estudiante no cumple los requisitos para inscribirse en este curso.");
        }

        if (enrollments.stream().anyMatch(e -> e.getStudentId().equals(student.getAccountId()))) {
            throw new IllegalStateException("El estudiante ya está inscrito en este curso.");
        }

        Enrollment newEnrollment = new Enrollment(null, student.getAccountId(), this.id);
        this.enrollments.add(newEnrollment);
        domainEvents.add(new CourseEvents.StudentEnrolledInCourse(student.getAccountId(), this.id));
        return newEnrollment;
    }

    public List<DomainEvent> getDomainEvents() { return List.copyOf(domainEvents); }
    public void clearDomainEvents() { domainEvents.clear(); }
    public Set<Enrollment> getEnrollments() { return Collections.unmodifiableSet(enrollments); }
}
