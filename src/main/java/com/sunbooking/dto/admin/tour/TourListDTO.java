package com.sunbooking.dto.admin.tour;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.sunbooking.entity.Tour;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lightweight DTO for displaying tours in list view.
 * Contains only essential information for listing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourListDTO {
    private Long id;
    private String title;
    private BigDecimal price;
    private Integer duration;
    private String location;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    // Statistics fields (optional, can be loaded separately)
    private Long bookingCount;
    private BigDecimal totalRevenue;
    private Double averageRating;

    /**
     * Convert Tour entity to TourListDTO.
     *
     * @param tour the tour entity
     * @return the tour list DTO
     */
    public static TourListDTO fromEntity(Tour tour) {
        if (tour == null) {
            return null;
        }

        return TourListDTO.builder()
                .id(tour.getId())
                .title(tour.getTitle())
                .price(tour.getPrice())
                .duration(tour.getDuration())
                .location(tour.getLocation())
                .isActive(tour.getIsActive())
                .createdAt(tour.getCreatedAt())
                .deletedAt(tour.getDeletedAt())
                .build();
    }

    /**
     * Check if the tour is deleted (soft-deleted).
     *
     * @return true if deleted, false otherwise
     */
    public boolean isDeleted() {
        return deletedAt != null || !Boolean.TRUE.equals(isActive);
    }
}
