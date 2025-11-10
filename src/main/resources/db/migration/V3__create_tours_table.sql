-- V3: Create tours table
-- Description: Table for storing bookable travel tours
CREATE TABLE tours (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME NULL,
    -- Constraints
    CONSTRAINT chk_tour_price CHECK (price >= 0.0)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
-- Indexes for performance optimization
CREATE INDEX idx_tours_is_active ON tours(is_active);
CREATE INDEX idx_tours_price ON tours(price);
CREATE INDEX idx_tours_created_at ON tours(created_at);
-- Full-text search index for title and description
CREATE FULLTEXT INDEX idx_tours_search ON tours(title, description);