package com.academia.domain.model.events;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.CourseId;
import java.time.Instant;

public class CourseEvents {
    public record TeacherAssignedToCourse(Long eventId, Instant occurredOn, CourseId courseId, AccountId teacherId) implements DomainEvent {
        public TeacherAssignedToCourse(CourseId courseId, AccountId teacherId) {
            this(null, Instant.now(), courseId, teacherId);
        }

        @Override
        public Long getEventId() {
            return eventId();
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn();
        }
    }
    public record StudentEnrolledInCourse(Long eventId, Instant occurredOn, AccountId studentId, CourseId courseId) implements DomainEvent {
        public StudentEnrolledInCourse(AccountId studentId, CourseId courseId) {
            this(null, Instant.now(), studentId, courseId);
        }

        @Override
        public Long getEventId() {
            return eventId();
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn();
        }
    }
}