package com.academia.domain.model.valueobjects.ids;

import lombok.Value;
import java.util.UUID;

@Value
public class ForumPostId {
    UUID value;
    
    public ForumPostId(String value) {
        this(UUID.fromString(value));
    }
    
    public ForumPostId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("ForumPostId cannot be null");
        }
        this.value = value;
    }
    
    public static ForumPostId of(String value) {
        return new ForumPostId(value);
    }
    
    public static ForumPostId of(UUID value) {
        return new ForumPostId(value);
    }
}