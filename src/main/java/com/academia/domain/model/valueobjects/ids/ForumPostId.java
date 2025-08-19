package com.academia.domain.model.valueobjects.ids;

import lombok.Value;

@Value
public class ForumPostId {
    Long value;
    
    public ForumPostId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("ForumPostId cannot be null");
        }
        this.value = value;
    }
    
    public static ForumPostId of(Long value) {
        return new ForumPostId(value);
    }
}