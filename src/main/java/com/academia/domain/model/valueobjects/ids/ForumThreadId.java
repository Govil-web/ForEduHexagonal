package com.academia.domain.model.valueobjects.ids;

import lombok.Value;

@Value
public class ForumThreadId {
    Long value;
    
    public ForumThreadId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("ForumThreadId cannot be null");
        }
        this.value = value;
    }
    
    public static ForumThreadId of(Long value) {
        return new ForumThreadId(value);
    }
}