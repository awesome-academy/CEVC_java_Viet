package com.sunbooking.dto.admin.review;

import com.sunbooking.entity.CategoryType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for review search criteria with pagination and sorting.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSearchCriteria {

    private String keyword; // Search in title or content
    private CategoryType categoryType;
    private Boolean isActive; // null = all, true = active only, false = deleted only

    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int size = 20;

    @Builder.Default
    private String sortBy = "createdAt";

    @Builder.Default
    private String sortDir = "DESC";
}
