package com.academia.domain.model.events;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.CourseId;
import java.time.Instant;
import java.util.UUID;

public class CourseEvents {
    public record TeacherAssignedToCourse(UUID eventId, Instant occurredOn, CourseId courseId, AccountId teacherId) implements DomainEvent {
        public TeacherAssignedToCourse(CourseId courseId, AccountId teacherId) {
            this(UUID.randomUUID(), Instant.now(), courseId, teacherId);
        }

        @Override
        public UUID getEventId() {
            return eventId();
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn();
        }
    }
    public record StudentEnrolledInCourse(UUID eventId, Instant occurredOn, AccountId studentId, CourseId courseId) implements DomainEvent {
        public StudentEnrolledInCourse(AccountId studentId, CourseId courseId) {
            this(UUID.randomUUID(), Instant.now(), studentId, courseId);
        }

        @Override
        public UUID getEventId() {
            return eventId();
        }

        @Override
        public Instant getOccurredOn() {
            return occurredOn();
        }
    }
}