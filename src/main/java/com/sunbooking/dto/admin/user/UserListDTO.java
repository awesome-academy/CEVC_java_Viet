package com.sunbooking.dto.admin.user;

import java.time.LocalDateTime;

import com.sunbooking.entity.User;
import com.sunbooking.entity.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lightweight DTO for displaying users in list views.
 * Contains only essential information for table display.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserListDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private UserRole role;
    private Boolean isActive;
    private LocalDateTime createdAt;

    /**
     * Convert User entity to UserListDTO.
     *
     * @param user the user entity
     * @return the user list DTO
     */
    public static UserListDTO fromEntity(User user) {
        UserListDTO dto = new UserListDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setIsActive(user.getIsActive());
        dto.setCreatedAt(user.getCreatedAt());
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
     * Get badge class for status.
     *
     * @return CSS class for status badge
     */
    public String getStatusBadgeClass() {
        return Boolean.TRUE.equals(isActive) ? "badge-success" : "badge-danger";
    }

    /**
     * Get role display name.
     *
     * @return formatted role name
     */
    public String getRoleDisplayName() {
        return role != null ? role.name() : "N/A";
    }

    /**
     * Get role badge class.
     *
     * @return CSS class for role badge
     */
    public String getRoleBadgeClass() {
        if (role == null) {
            return "badge-secondary";
        }
        return role == UserRole.ADMIN ? "badge-primary" : "badge-info";
    }
}
