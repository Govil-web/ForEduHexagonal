package com.academia.domain.model.valueobjects.ids;

import lombok.Value;
import java.util.UUID;

@Value
public class ForumThreadId {
    UUID value;
    
    public ForumThreadId(String value) {
        this(UUID.fromString(value));
    }
    
    public ForumThreadId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("ForumThreadId cannot be null");
        }
        this.value = value;
    }
    
    public static ForumThreadId of(String value) {
        return new ForumThreadId(value);
    }
    
    public static ForumThreadId of(UUID value) {
        return new ForumThreadId(value);
    }
}