package com.sunbooking.config;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Database connectivity test component
 * Tests database connection on application startup
 */
@Component
public class DatabaseConnectionTest {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionTest.class);

    private final DataSource dataSource;

    public DatabaseConnectionTest(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void testDatabaseConnection() {
        logger.info("========================================");
        logger.info("Testing Database Connection...");
        logger.info("========================================");

        try (Connection connection = dataSource.getConnection()) {
            String url = connection.getMetaData().getURL();
            String username = connection.getMetaData().getUserName();
            String databaseProductName = connection.getMetaData().getDatabaseProductName();
            String databaseProductVersion = connection.getMetaData().getDatabaseProductVersion();

            logger.info("✓ Database connection successful!");
            logger.info("  - Database: {}", databaseProductName);
            logger.info("  - Version: {}", databaseProductVersion);
            logger.info("  - URL: {}", url);
            logger.info("  - Username: {}", username);
            logger.info("  - Connection valid: {}", connection.isValid(5));

            // Test HikariCP
            logger.info("========================================");
            logger.info("HikariCP Connection Pool Information:");
            logger.info("========================================");
            logger.info("  - DataSource class: {}", dataSource.getClass().getName());

        } catch (SQLException e) {
            logger.error("========================================");
            logger.error("✗ Database connection failed!");
            logger.error("========================================");
            logger.error("Error message: {}", e.getMessage());
            logger.error("SQL State: {}", e.getSQLState());
            logger.error("Error Code: {}", e.getErrorCode());
            logger.error("");
            logger.error("Please check:");
            logger.error("  1. MySQL is running: sudo systemctl status mysql");
            logger.error("  2. Database 'sun_booking_tour' exists");
            logger.error("  3. Username and password in application.properties are correct");
            logger.error("  4. MySQL is accessible on localhost:3306");
            logger.error("");
            logger.error("To create the database, run:");
            logger.error(
                    "  sudo mysql -e \"CREATE DATABASE IF NOT EXISTS sun_booking_tour CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\"");
        }

        logger.info("========================================");
    }
}
