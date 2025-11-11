-- V8: Create additional indexes
-- Description: Additional indexes for performance optimization based on common query patterns
-- Composite indexes for common query patterns
-- Users: Find active users by role and creation date
CREATE INDEX idx_users_role_active_created ON users(role, is_active, created_at);
-- Tours: Find active tours by price range and creation date
CREATE INDEX idx_tours_active_price_created ON tours(is_active, price, created_at);
-- Bookings: Find user bookings by status and date
CREATE INDEX idx_bookings_user_status_date ON bookings(user_id, status, booking_date);
-- Bookings: Find tour bookings by status and date
CREATE INDEX idx_bookings_tour_status_date ON bookings(tour_id, status, booking_date);
-- Bookings: Payment tracking queries
CREATE INDEX idx_bookings_payment_status_date ON bookings(payment_status, booking_date);
-- Reviews: Find active reviews by user and category
CREATE INDEX idx_reviews_user_active ON reviews(user_id, is_active);
-- Reviews: Find active tour reviews
CREATE INDEX idx_reviews_tour_active ON reviews(tour_id, is_active);
-- Comments: Find active comments by user
CREATE INDEX idx_comments_user_active ON comments(user_id, is_active);
-- Likes: Count likes per review efficiently (covering index)
CREATE INDEX idx_likes_review_created ON likes(review_id, created_at);