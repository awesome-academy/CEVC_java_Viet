package com.sunbooking.dto.admin.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Search and filter criteria for admin list.
 * Used for pagination, sorting, and filtering.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminSearchCriteria {

    private String keyword; // Search in name or email
    private Boolean isActive; // Filter by active status
    private Integer page = 0;
    private Integer size = 25;
    private String sortBy = "createdAt";
    private String sortDir = "desc";

    /**
     * Check if keyword search is active.
     */
    public boolean hasKeyword() {
        return keyword != null && !keyword.trim().isEmpty();
    }

    /**
     * Check if active status filter is applied.
     */
    public boolean hasActiveFilter() {
        return isActive != null;
    }

    /**
     * Get trimmed keyword for search.
     */
    public String getTrimmedKeyword() {
        return hasKeyword() ? keyword.trim() : null;
    }
}
