-- V6: Create comments table
-- Description: Comments on reviews with support for nested replies
CREATE TABLE comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    user_id BIGINT NOT NULL,
    review_id BIGINT NOT NULL,
    parent_comment_id BIGINT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME NULL,
    -- Foreign key constraints
    CONSTRAINT fk_comments_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_comments_review_id FOREIGN KEY (review_id) REFERENCES reviews(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_comments_parent_comment_id FOREIGN KEY (parent_comment_id) REFERENCES comments(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
-- Indexes for performance optimization
CREATE INDEX idx_comments_user_id ON comments(user_id);
CREATE INDEX idx_comments_review_id ON comments(review_id);
CREATE INDEX idx_comments_parent_comment_id ON comments(parent_comment_id);
CREATE INDEX idx_comments_is_active ON comments(is_active);
CREATE INDEX idx_comments_created_at ON comments(created_at);
CREATE INDEX idx_comments_review_parent_active ON comments(review_id, parent_comment_id, is_active);