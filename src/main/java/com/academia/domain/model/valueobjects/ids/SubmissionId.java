package com.academia.domain.model.valueobjects.ids;

import lombok.Value;
import java.util.UUID;

@Value
public class SubmissionId {
    UUID value;
    
    public SubmissionId(String value) {
        this(UUID.fromString(value));
    }
    
    public SubmissionId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("SubmissionId cannot be null");
        }
        this.value = value;
    }
    
    public static SubmissionId of(String value) {
        return new SubmissionId(value);
    }
    
    public static SubmissionId of(UUID value) {
        return new SubmissionId(value);
    }
}