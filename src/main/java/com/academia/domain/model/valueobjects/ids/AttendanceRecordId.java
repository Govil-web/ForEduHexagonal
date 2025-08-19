package com.academia.domain.model.valueobjects.ids;

import lombok.Value;

@Value
public class AttendanceRecordId {
    Long value;
    
    public AttendanceRecordId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("AttendanceRecordId cannot be null");
        }
        this.value = value;
    }
    
    public static AttendanceRecordId of(Long value) {
        return new AttendanceRecordId(value);
    }
}