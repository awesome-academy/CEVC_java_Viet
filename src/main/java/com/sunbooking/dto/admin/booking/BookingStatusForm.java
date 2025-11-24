package com.sunbooking.dto.admin.booking;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.sunbooking.entity.BookingStatus;
import com.sunbooking.entity.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Form for updating booking status and payment status.
 * Includes validation for cancel reason when status is CANCELLED.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingStatusForm {

    @NotNull(message = "{validation.booking.status.required}")
    private BookingStatus status;

    @NotNull(message = "{validation.booking.payment.status.required}")
    private PaymentStatus paymentStatus;

    @Size(max = 1000, message = "{validation.booking.cancel.reason.size}")
    private String cancelReason;

    /**
     * Create form from BookingDTO for editing.
     *
     * @param dto the booking DTO
     * @return the booking status form
     */
    public static BookingStatusForm fromDTO(BookingDTO dto) {
        return BookingStatusForm.builder()
                .status(dto.getStatus())
                .paymentStatus(dto.getPaymentStatus())
                .cancelReason(dto.getCancelReason())
                .build();
    }
}
