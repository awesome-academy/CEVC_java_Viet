package com.sunbooking.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
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
 * Entity representing a bookable travel tour.
 * Implements soft-delete pattern using is_active and deleted_at fields.
 */
@Entity
@Table(name = "tours")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "{validation.tour.title.required}")
    @Size(max = 255, message = "{validation.tour.title.size}")
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @NotNull(message = "{validation.tour.description.required}")
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "{validation.tour.price.required}")
    @DecimalMin(value = "0.0", inclusive = true, message = "{validation.tour.price.min}")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

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
    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    private List<Booking> bookings = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    /**
     * Soft delete the tour by setting is_active to false and recording the deletion
     * timestamp.
     */
    public void softDelete() {
        if (!this.isActive) {
            throw new IllegalStateException("Tour is already deleted");
        }
        this.isActive = false;
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * Restore a soft-deleted tour by setting is_active to true and clearing the
     * deletion timestamp.
     */
    public void restore() {
        if (this.isActive) {
            throw new IllegalStateException("Tour is not deleted");
        }
        this.isActive = true;
        this.deletedAt = null;
    }

    /**
     * Check if the tour is active (not soft-deleted).
     *
     * @return true if tour is active, false otherwise
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(this.isActive);
    }
}
