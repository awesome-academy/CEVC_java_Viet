-- V9: Seed initial data
-- Description: Insert initial admin account and default categories
-- Insert initial admin account
-- Password: Admin@123 (BCrypt hashed with strength 10)
-- Note: Change this password immediately after first login in production
INSERT INTO users (
        name,
        email,
        password,
        phone,
        role,
        is_active,
        created_at,
        updated_at
    )
VALUES (
        'System Administrator',
        'admin@sunbooking.com',
        '$2a$10$rMJbXGKBXKX3PXgBvXKXzeTqYjPxL6L6xIY8qx8sMQxCzNvQZqRuC',
        '+84123456789',
        'ADMIN',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );
-- Insert default categories for tours
INSERT INTO categories (name, type, created_at, updated_at)
VALUES (
        'Adventure Tours',
        'TOUR',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'Cultural Tours',
        'TOUR',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'Beach & Island Tours',
        'TOUR',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'City Tours',
        'TOUR',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'Mountain & Trekking Tours',
        'TOUR',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );
-- Insert default categories for news
INSERT INTO categories (name, type, created_at, updated_at)
VALUES (
        'Travel Tips',
        'NEWS',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'Destination Guides',
        'NEWS',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'Travel News',
        'NEWS',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'Culture & History',
        'NEWS',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );
-- Insert default categories for food
INSERT INTO categories (name, type, created_at, updated_at)
VALUES (
        'Vietnamese Cuisine',
        'FOOD',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'Street Food',
        'FOOD',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'Fine Dining',
        'FOOD',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'Local Specialties',
        'FOOD',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'Vegetarian & Vegan',
        'FOOD',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );
-- Insert default categories for places
INSERT INTO categories (name, type, created_at, updated_at)
VALUES (
        'Historical Sites',
        'PLACE',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'Natural Wonders',
        'PLACE',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'Museums & Galleries',
        'PLACE',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'Entertainment & Nightlife',
        'PLACE',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'Shopping Districts',
        'PLACE',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );