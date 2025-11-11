package com.sunbooking.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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
 * Entity representing a category for content classification.
 * Categories can be of type TOUR, NEWS, FOOD, or PLACE.
 */
@Entity
@Table(name = "categories", uniqueConstraints = {
        @UniqueConstraint(name = "uq_category_name_type", columnNames = { "name", "type" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "{validation.category.name.required}")
    @Size(max = 255, message = "{validation.category.name.size}")
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @NotNull(message = "{validation.category.type.required}")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private CategoryType type;

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
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    /**
     * Soft delete the category by recording the deletion timestamp.
     */
    public void softDelete() {
        if (this.deletedAt != null) {
            throw new IllegalStateException("Category is already deleted");
        }
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * Restore a soft-deleted category by clearing the deletion timestamp.
     */
    public void restore() {
        if (this.deletedAt == null) {
            throw new IllegalStateException("Category is not deleted");
        }
        this.deletedAt = null;
    }

    /**
     * Check if the category is deleted (soft-deleted).
     *
     * @return true if category is deleted, false otherwise
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
