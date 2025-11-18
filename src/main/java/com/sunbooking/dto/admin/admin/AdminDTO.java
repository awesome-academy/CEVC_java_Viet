package com.sunbooking.dto.admin.admin;

import java.time.LocalDateTime;

import com.sunbooking.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for displaying detailed admin information.
 * Used in admin detail views.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    /**
     * Create AdminDTO from User entity.
     * Only for users with ADMIN role.
     */
    public static AdminDTO fromEntity(User user) {
        AdminDTO dto = new AdminDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setIsActive(user.getIsActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setDeletedAt(user.getDeletedAt());
        return dto;
    }

    /**
     * Get status text for display.
     */
    public String getStatusText() {
        if (deletedAt != null) {
            return "Deleted";
        }
        return isActive ? "Active" : "Inactive";
    }

    /**
     * Check if admin is deleted.
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }
}
