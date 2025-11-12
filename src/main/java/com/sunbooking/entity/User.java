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
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Entity representing a user in the system (both regular users and
 * administrators).
 * Implements soft-delete pattern using is_active and deleted_at fields.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "bookings", "reviews", "comments", "likes" })
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "{validation.name.required}")
    @Size(max = 255, message = "{validation.name.size}")
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @NotNull(message = "{validation.email.required}")
    @Email(message = "{validation.email.invalid}")
    @Size(max = 255, message = "{validation.email.size}")
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @NotNull(message = "{validation.password.required}")
    @Size(max = 255, message = "{validation.password.size}")
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Size(max = 50, message = "{validation.phone.size}")
    @Column(name = "phone", length = 50)
    private String phone;

    @NotNull(message = "{validation.role.required}")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role = UserRole.USER;

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
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    /**
     * Soft delete the user by setting is_active to false and recording the deletion
     * timestamp.
     */
    public void softDelete() {
        if (!this.isActive) {
            throw new IllegalStateException("User is already deleted");
        }
        this.isActive = false;
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * Restore a soft-deleted user by setting is_active to true and clearing the
     * deletion timestamp.
     */
    public void restore() {
        if (this.isActive) {
            throw new IllegalStateException("User is not deleted");
        }
        this.isActive = true;
        this.deletedAt = null;
    }

    /**
     * Check if the user has admin role.
     *
     * @return true if user is an admin, false otherwise
     */
    public boolean isAdmin() {
        return UserRole.ADMIN.equals(this.role);
    }

    /**
     * Check if the user account is active (not soft-deleted).
     *
     * @return true if user is active, false otherwise
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(this.isActive);
    }
}
