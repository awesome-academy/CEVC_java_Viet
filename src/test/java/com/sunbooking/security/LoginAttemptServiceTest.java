package com.sunbooking.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for LoginAttemptService rate limiting.
 */
@DisplayName("LoginAttemptService Tests")
public class LoginAttemptServiceTest {

    private LoginAttemptService loginAttemptService;

    @BeforeEach
    void setUp() {
        loginAttemptService = new LoginAttemptService();
    }

    @Test
    @DisplayName("Should allow login when no previous attempts")
    void testIsBlocked_NoAttempts() {
        // Given
        String ipAddress = "192.168.1.100";

        // When
        boolean blocked = loginAttemptService.isBlocked(ipAddress);

        // Then
        assertFalse(blocked);
        assertEquals(5, loginAttemptService.getRemainingAttempts(ipAddress));
    }

    @Test
    @DisplayName("Should record failed attempts correctly")
    void testRecordFailedAttempt() {
        // Given
        String ipAddress = "192.168.1.100";

        // When
        loginAttemptService.recordFailedAttempt(ipAddress);
        loginAttemptService.recordFailedAttempt(ipAddress);

        // Then
        assertFalse(loginAttemptService.isBlocked(ipAddress));
        assertEquals(3, loginAttemptService.getRemainingAttempts(ipAddress));
    }

    @Test
    @DisplayName("Should block IP after max failed attempts")
    void testIsBlocked_AfterMaxAttempts() {
        // Given
        String ipAddress = "192.168.1.100";

        // When - Record 5 failed attempts
        for (int i = 0; i < 5; i++) {
            loginAttemptService.recordFailedAttempt(ipAddress);
        }

        // Then
        assertTrue(loginAttemptService.isBlocked(ipAddress));
        assertEquals(0, loginAttemptService.getRemainingAttempts(ipAddress));
    }

    @Test
    @DisplayName("Should reset failed attempts on successful login")
    void testResetFailedAttempts() {
        // Given
        String ipAddress = "192.168.1.100";
        loginAttemptService.recordFailedAttempt(ipAddress);
        loginAttemptService.recordFailedAttempt(ipAddress);

        // When
        loginAttemptService.resetFailedAttempts(ipAddress);

        // Then
        assertFalse(loginAttemptService.isBlocked(ipAddress));
        assertEquals(5, loginAttemptService.getRemainingAttempts(ipAddress));
    }

    @Test
    @DisplayName("Should track different IPs independently")
    void testMultipleIPs() {
        // Given
        String ip1 = "192.168.1.100";
        String ip2 = "192.168.1.101";

        // When
        loginAttemptService.recordFailedAttempt(ip1);
        loginAttemptService.recordFailedAttempt(ip1);
        loginAttemptService.recordFailedAttempt(ip2);

        // Then
        assertEquals(3, loginAttemptService.getRemainingAttempts(ip1));
        assertEquals(4, loginAttemptService.getRemainingAttempts(ip2));
        assertFalse(loginAttemptService.isBlocked(ip1));
        assertFalse(loginAttemptService.isBlocked(ip2));
    }

    @Test
    @DisplayName("Should return correct statistics")
    void testGetStatistics() {
        // Given
        loginAttemptService.recordFailedAttempt("192.168.1.100");
        loginAttemptService.recordFailedAttempt("192.168.1.101");

        // When
        var stats = loginAttemptService.getStatistics();

        // Then
        assertNotNull(stats);
        assertEquals(2, stats.get("totalTrackedIPs"));
        assertEquals(5, stats.get("maxAttempts"));
        assertEquals(15, stats.get("lockoutDurationMinutes"));
    }

    @Test
    @DisplayName("Should handle edge case of exactly max attempts")
    void testExactlyMaxAttempts() {
        // Given
        String ipAddress = "192.168.1.100";

        // When - Record exactly 5 attempts
        for (int i = 0; i < 5; i++) {
            loginAttemptService.recordFailedAttempt(ipAddress);
        }

        // Then
        assertTrue(loginAttemptService.isBlocked(ipAddress));
        assertEquals(0, loginAttemptService.getRemainingAttempts(ipAddress));
    }

    @Test
    @DisplayName("Should not allow more than max attempts")
    void testMoreThanMaxAttempts() {
        // Given
        String ipAddress = "192.168.1.100";

        // When - Record more than 5 attempts
        for (int i = 0; i < 10; i++) {
            loginAttemptService.recordFailedAttempt(ipAddress);
        }

        // Then
        assertTrue(loginAttemptService.isBlocked(ipAddress));
        assertEquals(0, loginAttemptService.getRemainingAttempts(ipAddress));
    }
}
