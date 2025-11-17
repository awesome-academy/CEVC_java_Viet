package com.sunbooking.dto.admin.user;

import java.time.LocalDateTime;

import com.sunbooking.entity.User;
import com.sunbooking.entity.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for displaying detailed user information in admin panel.
 * Used for user detail views and responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private UserRole role;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // Statistics
    private long totalBookings;
    private long totalReviews;
    private long totalComments;

    /**
     * Convert User entity to UserDTO.
     *
     * @param user the user entity
     * @return the user DTO
     */
    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setIsActive(user.getIsActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setDeletedAt(user.getDeletedAt());
        return dto;
    }

    /**
     * Get formatted status text.
     *
     * @return "Active" if user is active, "Inactive" otherwise
     */
    public String getStatusText() {
        return Boolean.TRUE.equals(isActive) ? "Active" : "Inactive";
    }

    /**
     * Get role display name.
     *
     * @return formatted role name
     */
    public String getRoleDisplayName() {
        return role != null ? role.name() : "N/A";
    }
}
