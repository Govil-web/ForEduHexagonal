package com.academia.domain.model.valueobjects.ids;

import lombok.Value;

@Value
public class AnnouncementId {
    Long value;
    
    public AnnouncementId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("AnnouncementId cannot be null");
        }
        this.value = value;
    }
    
    public static AnnouncementId of(Long value) {
        return new AnnouncementId(value);
    }
}