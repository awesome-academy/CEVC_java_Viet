package com.sunbooking.dto.admin.admin;

import java.time.LocalDateTime;

import com.sunbooking.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lightweight DTO for admin list views.
 * Contains only essential information for table display.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminListDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private Boolean isActive;
    private LocalDateTime createdAt;

    /**
     * Create AdminListDTO from User entity.
     */
    public static AdminListDTO fromEntity(User user) {
        AdminListDTO dto = new AdminListDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setIsActive(user.getIsActive());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    /**
     * Get badge class for status display in UI.
     */
    public String getStatusBadgeClass() {
        return isActive ? "badge-success" : "badge-danger";
    }

    /**
     * Get status text.
     */
    public String getStatusText() {
        return isActive ? "Active" : "Inactive";
    }
}
