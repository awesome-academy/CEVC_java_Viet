package com.sunbooking.dto.admin.tour;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Search criteria for filtering tours.
 * Used in admin tour list view.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourSearchCriteria {
    private String keyword;
    private String status; // "active", "inactive", or null for all
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    @Builder.Default
    private Integer page = 0;

    @Builder.Default
    private Integer size = 20;

    @Builder.Default
    private String sortBy = "createdAt";

    @Builder.Default
    private String sortDir = "DESC";
}
