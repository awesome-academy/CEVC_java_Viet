-- V4: Create bookings table
-- Description: Table for storing user tour bookings with payment tracking
CREATE TABLE bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_code VARCHAR(20) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    tour_id BIGINT NOT NULL,
    booking_date DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'Booking status: PENDING, CONFIRMED, CANCELLED',
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'Payment status: PENDING, PAID, FAILED, REFUNDED',
    cancel_reason TEXT NULL,
    rating INT NULL COMMENT 'Tour rating (1-5)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    -- Foreign key constraints
    CONSTRAINT fk_bookings_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_bookings_tour_id FOREIGN KEY (tour_id) REFERENCES tours(id) ON DELETE CASCADE ON UPDATE CASCADE,
    -- Check constraints
    CONSTRAINT chk_booking_status CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED')),
    CONSTRAINT chk_payment_status CHECK (
        payment_status IN ('PENDING', 'PAID', 'FAILED', 'REFUNDED')
    ),
    CONSTRAINT chk_booking_rating CHECK (
        rating >= 1
        AND rating <= 5
    )
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
-- Indexes for performance optimization
CREATE UNIQUE INDEX idx_bookings_booking_code ON bookings(booking_code);
CREATE INDEX idx_bookings_user_id ON bookings(user_id);
CREATE INDEX idx_bookings_tour_id ON bookings(tour_id);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_bookings_payment_status ON bookings(payment_status);
CREATE INDEX idx_bookings_booking_date ON bookings(booking_date);
CREATE INDEX idx_bookings_user_created ON bookings(user_id, created_at);