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
 * Detailed DTO for booking information.
 * Includes user and tour detailed information for detail view.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetailDTO {

    private Long id;
    private String bookingCode;
    private Long userId;
    private String userName;
    private String userEmail;
    private String userPhone;
    private Long tourId;
    private String tourTitle;
    private String tourDescription;
    private BigDecimal tourPrice;
    private Integer tourDuration;
    private LocalDateTime bookingDate;
    private BookingStatus status;
    private PaymentStatus paymentStatus;
    private String cancelReason;
    private Integer rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Convert Booking entity to BookingDetailDTO using Builder pattern.
     * Note: Caller should validate booking integrity using BookingValidator before
     * calling this method.
     *
     * @param booking the booking entity (must not be null, must have user and tour)
     * @return the booking detail DTO
     */
    public static BookingDetailDTO fromEntity(Booking booking) {
        return BookingDetailDTO.builder()
                .id(booking.getId())
                .bookingCode(booking.getBookingCode())
                .userId(booking.getUser().getId())
                .userName(booking.getUser().getName())
                .userEmail(booking.getUser().getEmail())
                .userPhone(booking.getUser().getPhone())
                .tourId(booking.getTour().getId())
                .tourTitle(booking.getTour().getTitle())
                .tourDescription(booking.getTour().getDescription())
                .tourPrice(booking.getTour().getPrice())
                .tourDuration(booking.getTour().getDuration())
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
