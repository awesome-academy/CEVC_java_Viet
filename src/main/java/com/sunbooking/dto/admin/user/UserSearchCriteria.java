package com.sunbooking.dto.admin.user;

import com.sunbooking.entity.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user search and filtering criteria.
 * Used for advanced search functionality in user list.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchCriteria {

    private String keyword; // Search in name or email
    private UserRole role; // Filter by role
    private Boolean isActive; // Filter by active status
    private Integer page = 0; // Page number (0-indexed)
    private Integer size = 25; // Page size
    private String sortBy = "createdAt"; // Sort field
    private String sortDir = "desc"; // Sort direction (asc/desc)

    /**
     * Check if keyword search is active.
     *
     * @return true if keyword is not empty
     */
    public boolean hasKeyword() {
        return keyword != null && !keyword.trim().isEmpty();
    }

    /**
     * Check if role filter is active.
     *
     * @return true if role filter is set
     */
    public boolean hasRoleFilter() {
        return role != null;
    }

    /**
     * Check if active status filter is active.
     *
     * @return true if active filter is set
     */
    public boolean hasActiveFilter() {
        return isActive != null;
    }

    /**
     * Check if any filter is active.
     *
     * @return true if at least one filter is set
     */
    public boolean hasAnyFilter() {
        return hasKeyword() || hasRoleFilter() || hasActiveFilter();
    }

    /**
     * Get trimmed keyword.
     *
     * @return trimmed keyword or null
     */
    public String getTrimmedKeyword() {
        return keyword != null ? keyword.trim() : null;
    }
}
