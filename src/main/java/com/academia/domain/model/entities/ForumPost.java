package com.academia.domain.model.entities;

import com.academia.domain.model.valueobjects.ids.ForumPostId;
import com.academia.domain.model.valueobjects.ids.ForumThreadId;
import com.academia.domain.model.valueobjects.ids.AccountId;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Entity representing a post within a forum thread.
 * Contains the content, metadata, and relationship information for forum posts.
 */
@Getter
public class ForumPost {
    private final ForumPostId id;
    private final ForumThreadId threadId;
    private final AccountId authorId;
    private final ForumPostId parentPostId; // null for top-level posts
    private String content;
    private boolean isDeleted;
    private final LocalDateTime createdAt;
    private LocalDateTime editedAt;
    private AccountId editedBy;
    
    public ForumPost(ForumPostId id, ForumThreadId threadId, AccountId authorId, 
                    ForumPostId parentPostId, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Forum post content cannot be null or empty");
        }
        
        this.id = id;
        this.threadId = threadId;
        this.authorId = authorId;
        this.parentPostId = parentPostId;
        this.content = content.trim();
        this.isDeleted = false;
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Edits the post content.
     */
    public void editContent(String newContent, AccountId editedBy) {
        if (isDeleted) {
            throw new IllegalStateException("Cannot edit a deleted post");
        }
        
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("Post content cannot be null or empty");
        }
        
        this.content = newContent.trim();
        this.editedAt = LocalDateTime.now();
        this.editedBy = editedBy;
    }
    
    /**
     * Marks the post as deleted.
     */
    public void markAsDeleted() {
        this.isDeleted = true;
    }
    
    /**
     * Checks if this is a reply to another post.
     */
    public boolean isReply() {
        return parentPostId != null;
    }
    
    /**
     * Checks if this is a top-level post.
     */
    public boolean isTopLevel() {
        return parentPostId == null;
    }
    
    /**
     * Checks if the post has been edited.
     */
    public boolean hasBeenEdited() {
        return editedAt != null;
    }
    
    /**
     * Checks if the post can be edited by the given user.
     */
    public boolean canBeEditedBy(AccountId userId) {
        return !isDeleted && authorId.equals(userId);
    }
}