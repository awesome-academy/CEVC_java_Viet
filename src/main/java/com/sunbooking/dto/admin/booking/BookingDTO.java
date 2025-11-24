package com.sunbooking.dto.admin.booking;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.sunbooking.entity.Booking;
import com.sunbooking.entity.BookingStatus;
import com.sunbooking.entity.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for booking basic information.
 * Used for booking detail view and update operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {

    private Long id;
    private String bookingCode;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long tourId;
    private String tourTitle;
    private BigDecimal tourPrice;
    private LocalDateTime bookingDate;
    private BookingStatus status;
    private PaymentStatus paymentStatus;
    private String cancelReason;
    private Integer rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Convert Booking entity to BookingDTO using Builder pattern.
     * Note: Caller should validate booking integrity using BookingValidator before
     * calling this method.
     *
     * @param booking the booking entity (must not be null, must have user and tour)
     * @return the booking DTO
     */
    public static BookingDTO fromEntity(Booking booking) {
        return BookingDTO.builder()
                .id(booking.getId())
                .bookingCode(booking.getBookingCode())
                .userId(booking.getUser().getId())
                .userName(booking.getUser().getName())
                .userEmail(booking.getUser().getEmail())
                .tourId(booking.getTour().getId())
                .tourTitle(booking.getTour().getTitle())
                .tourPrice(booking.getTour().getPrice())
                .bookingDate(booking.getBookingDate())
                .status(booking.getStatus())
                .paymentStatus(booking.getPaymentStatus())
                .cancelReason(booking.getCancelReason())
                .rating(booking.getRating())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
}
