-- V2: Create categories table
-- Description: Categories for different content types (Tours, News, Food, Places)
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL COMMENT 'Category type: TOUR, NEWS, FOOD, or PLACE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME NULL,
    -- Constraints
    CONSTRAINT chk_category_type CHECK (type IN ('TOUR', 'NEWS', 'FOOD', 'PLACE')),
    CONSTRAINT uq_category_name_type UNIQUE (name, type)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
-- Indexes for performance optimization
CREATE INDEX idx_categories_type ON categories(type);
CREATE INDEX idx_categories_deleted_at ON categories(deleted_at);
CREATE INDEX idx_categories_type_deleted ON categories(type, deleted_at);