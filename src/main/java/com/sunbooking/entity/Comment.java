package com.sunbooking.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a comment on a review.
 * Supports nested comments through self-referencing parent-child relationship.
 * Implements soft-delete pattern using is_active and deleted_at fields.
 */
@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "{validation.comment.content.required}")
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @NotNull(message = "{validation.user.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_comments_user_id"))
    @JsonIgnore
    private User user;

    @NotNull(message = "{validation.review.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false, foreignKey = @ForeignKey(name = "fk_comments_review_id"))
    @JsonIgnore
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id", foreignKey = @ForeignKey(name = "fk_comments_parent_comment_id"))
    @JsonIgnore
    private Comment parentComment;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    // Relationships

    @JsonIgnore
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
    private List<Comment> replies = new ArrayList<>();

    /**
     * Soft delete the comment by setting is_active to false and recording the
     * deletion timestamp.
     */
    public void softDelete() {
        if (!this.isActive) {
            throw new IllegalStateException("Comment is already deleted");
        }
        this.isActive = false;
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * Restore a soft-deleted comment by setting is_active to true and clearing the
     * deletion timestamp.
     */
    public void restore() {
        if (this.isActive) {
            throw new IllegalStateException("Comment is not deleted");
        }
        this.isActive = true;
        this.deletedAt = null;
    }

    /**
     * Check if the comment is active (not soft-deleted).
     *
     * @return true if comment is active, false otherwise
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(this.isActive);
    }

    /**
     * Check if this is a top-level comment (not a reply).
     *
     * @return true if comment has no parent, false otherwise
     */
    public boolean isTopLevel() {
        return this.parentComment == null;
    }

    /**
     * Check if this comment is a reply to another comment.
     *
     * @return true if comment has a parent, false otherwise
     */
    public boolean isReply() {
        return this.parentComment != null;
    }

    /**
     * Get the number of replies to this comment.
     *
     * @return the count of replies
     */
    public int getReplyCount() {
        return this.replies != null ? this.replies.size() : 0;
    }
}
