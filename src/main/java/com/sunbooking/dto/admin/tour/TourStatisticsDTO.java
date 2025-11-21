package com.sunbooking.dto.admin.tour;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for tour statistics.
 * Contains aggregated data about bookings, revenue, and ratings.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourStatisticsDTO {
    private Long bookingCount;
    private BigDecimal totalRevenue;
    private Double averageRating;
    private Long reviewCount;

    /**
     * Get revenue as BigDecimal (zero if null).
     *
     * @return revenue or zero
     */
    public BigDecimal getRevenue() {
        return totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
    }

    /**
     * Check if tour has any bookings.
     *
     * @return true if has bookings
     */
    public boolean hasBookings() {
        return bookingCount != null && bookingCount > 0;
    }
}
