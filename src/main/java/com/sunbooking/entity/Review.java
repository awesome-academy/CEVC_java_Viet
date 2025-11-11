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
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a user-generated review.
 * Can be associated with a category and optionally with a tour.
 * Implements soft-delete pattern using is_active and deleted_at fields.
 */
@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "{validation.user.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reviews_user_id"))
    @JsonIgnore
    private User user;

    @NotNull(message = "{validation.category.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reviews_category_id"))
    @JsonIgnore
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", foreignKey = @ForeignKey(name = "fk_reviews_tour_id"))
    @JsonIgnore
    private Tour tour;

    @NotNull(message = "{validation.review.title.required}")
    @Size(max = 255, message = "{validation.review.title.size}")
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @NotNull(message = "{validation.review.content.required}")
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

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
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    /**
     * Soft delete the review by setting is_active to false and recording the
     * deletion timestamp.
     */
    public void softDelete() {
        if (!this.isActive) {
            throw new IllegalStateException("Review is already deleted");
        }
        this.isActive = false;
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * Restore a soft-deleted review by setting is_active to true and clearing the
     * deletion timestamp.
     */
    public void restore() {
        if (this.isActive) {
            throw new IllegalStateException("Review is not deleted");
        }
        this.isActive = true;
        this.deletedAt = null;
    }

    /**
     * Check if the review is active (not soft-deleted).
     *
     * @return true if review is active, false otherwise
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(this.isActive);
    }

    /**
     * Check if this is a tour review.
     *
     * @return true if review is associated with a tour, false otherwise
     */
    public boolean isTourReview() {
        return this.tour != null;
    }

    /**
     * Get the number of likes for this review.
     *
     * @return the count of likes
     */
    public int getLikeCount() {
        return this.likes != null ? this.likes.size() : 0;
    }

    /**
     * Get the number of comments for this review.
     *
     * @return the count of comments
     */
    public int getCommentCount() {
        return this.comments != null ? this.comments.size() : 0;
    }
}
