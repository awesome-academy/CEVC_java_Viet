-- V7: Create likes table
-- Description: User likes on reviews with unique constraint to prevent duplicate likes
CREATE TABLE likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    review_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- Foreign key constraints
    CONSTRAINT fk_likes_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_likes_review_id FOREIGN KEY (review_id) REFERENCES reviews(id) ON DELETE CASCADE ON UPDATE CASCADE,
    -- Unique constraint to prevent duplicate likes
    CONSTRAINT uq_likes_user_review UNIQUE (user_id, review_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
-- Indexes for performance optimization
CREATE INDEX idx_likes_user_id ON likes(user_id);
CREATE INDEX idx_likes_review_id ON likes(review_id);