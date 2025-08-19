package com.academia.domain.model.valueobjects.ids;

import lombok.Value;

@Value
public class SubmissionId {
    Long value;
    
    public SubmissionId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("SubmissionId cannot be null");
        }
        this.value = value;
    }
    
    public static SubmissionId of(Long value) {
        return new SubmissionId(value);
    }
}