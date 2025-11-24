package com.sunbooking.dto.admin.booking;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Search criteria for filtering bookings.
 * Used for dynamic query building with JPA Specification.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingSearchCriteria {

    @Builder.Default
    private String keyword = "";

    private String status;
    private String paymentStatus;
    private LocalDate fromDate;
    private LocalDate toDate;

    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int size = 20;

    @Builder.Default
    private String sortBy = "createdAt";

    @Builder.Default
    private String sortDir = "DESC";
}
