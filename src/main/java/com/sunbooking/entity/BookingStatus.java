package com.sunbooking.entity;

/**
 * Enum representing booking status in the system.
 */
public enum BookingStatus {
    /**
     * Booking has been created but not yet confirmed
     */
    PENDING,

    /**
     * Booking has been confirmed by admin or system
     */
    CONFIRMED,

    /**
     * Booking has been cancelled by user or admin
     */
    CANCELLED
}
