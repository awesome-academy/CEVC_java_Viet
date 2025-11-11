package com.sunbooking.entity;

/**
 * Enum representing payment status for bookings.
 */
public enum PaymentStatus {
    /**
     * Payment is pending - awaiting payment from customer
     */
    PENDING,

    /**
     * Payment has been successfully completed
     */
    PAID,

    /**
     * Payment attempt failed
     */
    FAILED,

    /**
     * Payment has been refunded to customer
     */
    REFUNDED
}
