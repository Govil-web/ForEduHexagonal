package com.academia.domain.model.valueobjects.ids;

import lombok.Value;
import java.util.UUID;

@Value
public class AttendanceRecordId {
    UUID value;
    
    public AttendanceRecordId(String value) {
        this(UUID.fromString(value));
    }
    
    public AttendanceRecordId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("AttendanceRecordId cannot be null");
        }
        this.value = value;
    }
    
    public static AttendanceRecordId of(String value) {
        return new AttendanceRecordId(value);
    }
    
    public static AttendanceRecordId of(UUID value) {
        return new AttendanceRecordId(value);
    }
}