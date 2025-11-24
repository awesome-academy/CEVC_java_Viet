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
 * Lightweight DTO for displaying bookings in list view.
 * Contains essential information for listing bookings in admin panel.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingListDTO {

    private Long id;
    private String bookingCode;
    private String userName;
    private String userEmail;
    private String tourTitle;
    private BigDecimal tourPrice;
    private LocalDateTime bookingDate;
    private BookingStatus status;
    private PaymentStatus paymentStatus;
    private Integer rating;
    private LocalDateTime createdAt;

    /**
     * Convert Booking entity to BookingListDTO using Builder pattern.
     * Note: Caller should validate booking integrity using BookingValidator before
     * calling this method.
     *
     * @param booking the booking entity (must not be null, must have user and tour)
     * @return the booking list DTO
     */
    public static BookingListDTO fromEntity(Booking booking) {
        return BookingListDTO.builder()
                .id(booking.getId())
                .bookingCode(booking.getBookingCode())
                .userName(booking.getUser().getName())
                .userEmail(booking.getUser().getEmail())
                .tourTitle(booking.getTour().getTitle())
                .tourPrice(booking.getTour().getPrice())
                .bookingDate(booking.getBookingDate())
                .status(booking.getStatus())
                .paymentStatus(booking.getPaymentStatus())
                .rating(booking.getRating())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
