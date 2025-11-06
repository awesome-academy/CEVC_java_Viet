package com.sunbooking.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for database operations and setup
 */
public class DatabaseUtils {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);

    /**
     * Test database connection with given parameters
     *
     * @param url      JDBC URL
     * @param username Database username
     * @param password Database password
     * @return true if connection successful, false otherwise
     */
    public static boolean testConnection(String url, String username, String password) {
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            logger.info("Database connection test successful");
            logger.info("Database: {}", conn.getMetaData().getDatabaseProductName());
            logger.info("Version: {}", conn.getMetaData().getDatabaseProductVersion());
            return true;
        } catch (SQLException e) {
            logger.error("Database connection test failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Create database if it doesn't exist
     * Note: This requires connection to MySQL server (not specific database)
     *
     * @param host     Database host
     * @param port     Database port
     * @param dbName   Database name to create
     * @param username Username with CREATE DATABASE privilege
     * @param password Password
     * @return true if database created or already exists, false otherwise
     */
    public static boolean createDatabaseIfNotExists(String host, int port, String dbName,
            String username, String password) {
        String mysqlUrl = String.format(
                "jdbc:mysql://%s:%d?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                host, port);

        try (Connection conn = DriverManager.getConnection(mysqlUrl, username, password);
                Statement stmt = conn.createStatement()) {

            String createDbSql = String.format(
                    "CREATE DATABASE IF NOT EXISTS %s CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",
                    dbName);

            stmt.executeUpdate(createDbSql);
            logger.info("Database '{}' created successfully or already exists", dbName);
            return true;

        } catch (SQLException e) {
            logger.error("Failed to create database '{}': {}", dbName, e.getMessage());
            return false;
        }
    }

    /**
     * Check if database exists
     *
     * @param host     Database host
     * @param port     Database port
     * @param dbName   Database name to check
     * @param username Database username
     * @param password Database password
     * @return true if database exists, false otherwise
     */
    public static boolean databaseExists(String host, int port, String dbName,
            String username, String password) {
        String mysqlUrl = String.format(
                "jdbc:mysql://%s:%d?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                host, port);

        try (Connection conn = DriverManager.getConnection(mysqlUrl, username, password);
                Statement stmt = conn.createStatement()) {

            var rs = stmt.executeQuery(String.format("SHOW DATABASES LIKE '%s'", dbName));
            boolean exists = rs.next();

            if (exists) {
                logger.info("Database '{}' exists", dbName);
            } else {
                logger.warn("Database '{}' does not exist", dbName);
            }

            return exists;

        } catch (SQLException e) {
            logger.error("Failed to check database '{}': {}", dbName, e.getMessage());
            return false;
        }
    }

    /**
     * Print database connection information for troubleshooting
     */
    public static void printConnectionInfo(Connection connection) throws SQLException {
        if (connection == null || connection.isClosed()) {
            logger.warn("Connection is null or closed");
            return;
        }

        var metadata = connection.getMetaData();

        logger.info("=================================================");
        logger.info("Database Connection Information:");
        logger.info("=================================================");
        logger.info("Database Product: {}", metadata.getDatabaseProductName());
        logger.info("Database Version: {}", metadata.getDatabaseProductVersion());
        logger.info("Driver Name: {}", metadata.getDriverName());
        logger.info("Driver Version: {}", metadata.getDriverVersion());
        logger.info("URL: {}", metadata.getURL());
        logger.info("Username: {}", metadata.getUserName());
        logger.info("Catalog: {}", connection.getCatalog());
        logger.info("Schema: {}", connection.getSchema());
        logger.info("Auto Commit: {}", connection.getAutoCommit());
        logger.info("Read Only: {}", connection.isReadOnly());
        logger.info("Transaction Isolation: {}", connection.getTransactionIsolation());
        logger.info("Connection Valid: {}", connection.isValid(5));
        logger.info("=================================================");
    }

    /**
     * Get database size in MB
     *
     * @param connection Active database connection
     * @param dbName     Database name
     * @return Database size in MB, or -1 if unable to determine
     */
    public static double getDatabaseSizeMB(Connection connection, String dbName) {
        String query = "SELECT SUM(data_length + index_length) / 1024 / 1024 AS size_mb " +
                "FROM information_schema.tables " +
                "WHERE table_schema = ?";

        try (var stmt = connection.prepareStatement(query)) {
            stmt.setString(1, dbName);
            var rs = stmt.executeQuery();

            if (rs.next()) {
                double sizeMB = rs.getDouble("size_mb");
                logger.info("Database '{}' size: {:.2f} MB", dbName, sizeMB);
                return sizeMB;
            }
        } catch (SQLException e) {
            logger.error("Failed to get database size: {}", e.getMessage());
        }

        return -1;
    }
}
