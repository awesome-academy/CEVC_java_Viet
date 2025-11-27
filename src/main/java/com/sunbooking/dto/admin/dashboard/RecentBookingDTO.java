package com.sunbooking.dto.admin.dashboard;

import java.time.LocalDateTime;

import com.sunbooking.entity.Booking;
import com.sunbooking.entity.BookingStatus;
import com.sunbooking.entity.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for displaying recent bookings on the dashboard.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentBookingDTO {

    private Long id;
    private String bookingCode;
    private String userName;
    private String userEmail;
    private String tourTitle;
    private LocalDateTime bookingDate;
    private BookingStatus status;
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;

    /**
     * Create DTO from entity.
     *
     * @param booking the booking entity
     * @return DTO instance
     */
    public static RecentBookingDTO fromEntity(Booking booking) {
        return RecentBookingDTO.builder()
                .id(booking.getId())
                .bookingCode(booking.getBookingCode())
                .userName(booking.getUser() != null ? booking.getUser().getName() : "N/A")
                .userEmail(booking.getUser() != null ? booking.getUser().getEmail() : "N/A")
                .tourTitle(booking.getTour() != null ? booking.getTour().getTitle() : "N/A")
                .bookingDate(booking.getBookingDate())
                .status(booking.getStatus())
                .paymentStatus(booking.getPaymentStatus())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
