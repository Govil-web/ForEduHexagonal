package com.academia.domain.model.valueobjects.ids;

import lombok.Value;
import java.util.UUID;

@Value
public class AnnouncementId {
    UUID value;
    
    public AnnouncementId(String value) {
        this(UUID.fromString(value));
    }
    
    public AnnouncementId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("AnnouncementId cannot be null");
        }
        this.value = value;
    }
    
    public static AnnouncementId of(String value) {
        return new AnnouncementId(value);
    }
    
    public static AnnouncementId of(UUID value) {
        return new AnnouncementId(value);
    }
}