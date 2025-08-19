package com.academia.domain.model.aggregates;

import com.academia.domain.model.entities.ForumPost;
import com.academia.domain.model.events.DomainEvent;
import com.academia.domain.model.events.ForumEvents;
import com.academia.domain.model.valueobjects.ids.ForumThreadId;
import com.academia.domain.model.valueobjects.ids.ForumPostId;
import com.academia.domain.model.valueobjects.ids.AccountId;
import com.academia.domain.model.valueobjects.ids.CourseId;
import com.academia.domain.model.enums.ForumThreadStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ForumThread aggregate root representing a discussion thread within a course forum.
 * Manages thread lifecycle, posts, and moderation actions.
 */
@Getter
public class ForumThread {
    private final ForumThreadId id;
    private final CourseId courseId;
    private final AccountId authorId;
    private String title;
    private String description;
    private ForumThreadStatus status;
    private final Map<ForumPostId, ForumPost> posts;
    private LocalDateTime lastActivityAt;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    
    public ForumThread(ForumThreadId id, CourseId courseId, AccountId authorId, 
                      String title, String description) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Forum thread title cannot be null or empty");
        }
        
        this.id = id;
        this.courseId = courseId;
        this.authorId = authorId;
        this.title = title.trim();
        this.description = description != null ? description.trim() : "";
        this.status = ForumThreadStatus.OPEN;
        this.posts = new LinkedHashMap<>();
        this.createdAt = LocalDateTime.now();
        this.lastActivityAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new ForumEvents.ForumThreadCreatedEvent(id, courseId, authorId, title));
    }
    
    /**
     * Adds a new post to the thread.
     */
    public ForumPost addPost(ForumPostId postId, AccountId authorId, ForumPostId parentPostId, String content) {
        if (!status.allowsNewPosts()) {
            throw new IllegalStateException("Cannot add posts to thread in status: " + status);
        }
        
        // Validate parent post exists if this is a reply
        if (parentPostId != null && !posts.containsKey(parentPostId)) {
            throw new IllegalArgumentException("Parent post not found in this thread");
        }
        
        ForumPost newPost = new ForumPost(postId, this.id, authorId, parentPostId, content);
        posts.put(postId, newPost);
        
        this.lastActivityAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new ForumEvents.ForumPostCreatedEvent(
            postId, this.id, authorId, parentPostId != null));
        
        return newPost;
    }
    
    /**
     * Edits an existing post in the thread.
     */
    public void editPost(ForumPostId postId, String newContent, AccountId editedBy) {
        ForumPost post = posts.get(postId);
        if (post == null) {
            throw new IllegalArgumentException("Post not found in this thread");
        }
        
        if (!post.canBeEditedBy(editedBy)) {
            throw new IllegalStateException("User cannot edit this post");
        }
        
        post.editContent(newContent, editedBy);
        this.lastActivityAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new ForumEvents.ForumPostEditedEvent(postId, this.id, editedBy));
    }
    
    /**
     * Deletes a post from the thread.
     */
    public void deletePost(ForumPostId postId, AccountId deletedBy) {
        ForumPost post = posts.get(postId);
        if (post == null) {
            throw new IllegalArgumentException("Post not found in this thread");
        }
        
        post.markAsDeleted();
        this.lastActivityAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new ForumEvents.ForumPostDeletedEvent(postId, this.id, deletedBy));
    }
    
    /**
     * Updates the thread title and description.
     */
    public void updateDetails(String newTitle, String newDescription, AccountId updatedBy) {
        if (newTitle == null || newTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Thread title cannot be null or empty");
        }
        
        this.title = newTitle.trim();
        this.description = newDescription != null ? newDescription.trim() : "";
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Closes the thread preventing new posts.
     */
    public void close(AccountId closedBy) {
        if (status == ForumThreadStatus.CLOSED) {
            return; // Already closed
        }
        
        this.status = ForumThreadStatus.CLOSED;
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new ForumEvents.ForumThreadClosedEvent(id, closedBy));
    }
    
    /**
     * Locks the thread (moderator action).
     */
    public void lock(AccountId lockedBy, String reason) {
        this.status = ForumThreadStatus.LOCKED;
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new ForumEvents.ForumThreadLockedEvent(id, lockedBy, reason));
    }
    
    /**
     * Pins the thread to the top of the forum.
     */
    public void pin(AccountId pinnedBy) {
        if (status == ForumThreadStatus.PINNED) {
            return; // Already pinned
        }
        
        this.status = ForumThreadStatus.PINNED;
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new ForumEvents.ForumThreadPinnedEvent(id, pinnedBy));
    }
    
    /**
     * Unpins the thread.
     */
    public void unpin(AccountId unpinnedBy) {
        if (status != ForumThreadStatus.PINNED) {
            return; // Not pinned
        }
        
        this.status = ForumThreadStatus.OPEN;
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new ForumEvents.ForumThreadUnpinnedEvent(id, unpinnedBy));
    }
    
    /**
     * Reopens a closed thread.
     */
    public void reopen(AccountId reopenedBy) {
        if (status == ForumThreadStatus.CLOSED) {
            this.status = ForumThreadStatus.OPEN;
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    /**
     * Archives the thread.
     */
    public void archive(AccountId archivedBy) {
        this.status = ForumThreadStatus.ARCHIVED;
        this.updatedAt = LocalDateTime.now();
        
        domainEvents.add(new ForumEvents.ForumThreadArchivedEvent(id, archivedBy));
    }
    
    /**
     * Gets the total number of posts in the thread.
     */
    public int getPostCount() {
        return (int) posts.values().stream()
            .filter(post -> !post.isDeleted())
            .count();
    }
    
    /**
     * Gets the number of replies (excluding top-level posts).
     */
    public int getReplyCount() {
        return (int) posts.values().stream()
            .filter(post -> !post.isDeleted() && post.isReply())
            .count();
    }
    
    /**
     * Gets all active (non-deleted) posts.
     */
    public List<ForumPost> getActivePosts() {
        return posts.values().stream()
            .filter(post -> !post.isDeleted())
            .toList();
    }
    
    /**
     * Checks if the thread allows new posts.
     */
    public boolean allowsNewPosts() {
        return status.allowsNewPosts();
    }
    
    /**
     * Checks if the thread is active.
     */
    public boolean isActive() {
        return status.isActive();
    }
    
    /**
     * Checks if the thread is pinned.
     */
    public boolean isPinned() {
        return status == ForumThreadStatus.PINNED;
    }
    
    public List<DomainEvent> getDomainEvents() {
        return List.copyOf(domainEvents);
    }
    
    public void clearDomainEvents() {
        domainEvents.clear();
    }
    
    public List<ForumPost> getPosts() {
        return List.copyOf(posts.values());
    }
}