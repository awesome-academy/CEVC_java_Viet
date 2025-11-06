-- Sun Booking Tour Database Setup Script
-- Run this script to create the database
-- Create database
CREATE DATABASE IF NOT EXISTS sun_booking_tour CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- Create application user (optional - for production)
-- CREATE USER IF NOT EXISTS 'sunbooking'@'localhost' IDENTIFIED BY 'your_password_here';
-- GRANT ALL PRIVILEGES ON sun_booking_tour.* TO 'sunbooking'@'localhost';
-- FLUSH PRIVILEGES;
-- Verify database creation
SHOW DATABASES LIKE 'sun_booking_tour';
-- Use the database
USE sun_booking_tour;
-- Show database character set
SELECT @@character_set_database,
    @@collation_database;