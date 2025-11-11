package com.sunbooking.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a user's tour booking.
 * Includes booking status, payment status, and optional rating.
 */
@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "{validation.booking.code.required}")
    @Size(max = 20, message = "{validation.booking.code.size}")
    @Column(name = "booking_code", nullable = false, unique = true, length = 20)
    private String bookingCode;

    @NotNull(message = "{validation.user.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_bookings_user_id"))
    @JsonIgnore
    private User user;

    @NotNull(message = "{validation.tour.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false, foreignKey = @ForeignKey(name = "fk_bookings_tour_id"))
    @JsonIgnore
    private Tour tour;

    @NotNull(message = "{validation.booking.date.required}")
    @Column(name = "booking_date", nullable = false)
    private LocalDateTime bookingDate;

    @NotNull(message = "{validation.booking.status.required}")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BookingStatus status = BookingStatus.PENDING;

    @NotNull(message = "{validation.booking.payment.status.required}")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "cancel_reason", columnDefinition = "TEXT")
    private String cancelReason;

    @Min(value = 1, message = "{validation.booking.rating.min}")
    @Max(value = 5, message = "{validation.booking.rating.max}")
    @Column(name = "rating")
    private Integer rating;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Check if the booking is confirmed.
     *
     * @return true if booking status is CONFIRMED, false otherwise
     */
    public boolean isConfirmed() {
        return BookingStatus.CONFIRMED.equals(this.status);
    }

    /**
     * Check if the booking is cancelled.
     *
     * @return true if booking status is CANCELLED, false otherwise
     */
    public boolean isCancelled() {
        return BookingStatus.CANCELLED.equals(this.status);
    }

    /**
     * Check if the booking is pending.
     *
     * @return true if booking status is PENDING, false otherwise
     */
    public boolean isPending() {
        return BookingStatus.PENDING.equals(this.status);
    }

    /**
     * Check if payment is completed.
     *
     * @return true if payment status is PAID, false otherwise
     */
    public boolean isPaid() {
        return PaymentStatus.PAID.equals(this.paymentStatus);
    }

    /**
     * Cancel the booking with a reason.
     *
     * @param reason the reason for cancellation
     */
    public void cancel(String reason) {
        if (this.isCancelled()) {
            throw new IllegalStateException("Booking is already cancelled");
        }
        this.status = BookingStatus.CANCELLED;
        this.cancelReason = reason;
    }

    /**
     * Confirm the booking.
     */
    public void confirm() {
        if (this.isConfirmed()) {
            throw new IllegalStateException("Booking is already confirmed");
        }
        if (this.isCancelled()) {
            throw new IllegalStateException("Cannot confirm a cancelled booking");
        }
        this.status = BookingStatus.CONFIRMED;
    }

    /**
     * Mark payment as paid.
     */
    public void markAsPaid() {
        if (this.isPaid()) {
            throw new IllegalStateException("Payment is already paid");
        }
        this.paymentStatus = PaymentStatus.PAID;
    }
}
