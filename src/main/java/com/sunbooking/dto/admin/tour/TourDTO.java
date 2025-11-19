package com.sunbooking.dto.admin.tour;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.sunbooking.entity.Tour;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Tour entity.
 * Used for displaying tour information in admin views.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourDTO {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private Integer duration;
    private Integer maxParticipants;
    private String location;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    /**
     * Convert Tour entity to TourDTO.
     *
     * @param tour the tour entity
     * @return the tour DTO
     */
    public static TourDTO fromEntity(Tour tour) {
        if (tour == null) {
            return null;
        }

        return TourDTO.builder()
                .id(tour.getId())
                .title(tour.getTitle())
                .description(tour.getDescription())
                .price(tour.getPrice())
                .duration(tour.getDuration())
                .maxParticipants(tour.getMaxParticipants())
                .location(tour.getLocation())
                .isActive(tour.getIsActive())
                .createdAt(tour.getCreatedAt())
                .updatedAt(tour.getUpdatedAt())
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
