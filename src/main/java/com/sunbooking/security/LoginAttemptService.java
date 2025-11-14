package com.sunbooking.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Custom authentication failure handler with rate limiting.
 * 
 * This handler tracks failed login attempts per IP address and implements
 * rate limiting to prevent brute force attacks. After a configurable number
 * of failed attempts, the IP address is temporarily blocked.
 * 
 * Features:
 * - Tracks failed login attempts per IP address
 * - Blocks IP after max failed attempts
 * - Auto-unblocks after lockout duration
 * - Provides informative error messages
 * - Logs security events
 * 
 * @author Sun Booking Team
 * @version 1.0
 * @since 2025-11-12
 */
@Component
@Slf4j
public class LoginAttemptService extends SimpleUrlAuthenticationFailureHandler {

    // Maximum failed login attempts before blocking
    private static final int MAX_ATTEMPTS = 5;

    // Lockout duration in minutes
    private static final int LOCKOUT_DURATION_MINUTES = 15;

    // Store failed attempts: IP -> AttemptInfo
    private final Map<String, AttemptInfo> attemptsCache = new ConcurrentHashMap<>();

    /**
     * Handle authentication failure.
     * 
     * @param request   the HTTP request
     * @param response  the HTTP response
     * @param exception the authentication exception
     * @throws IOException      if an I/O error occurs
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        String ipAddress = getClientIP(request);
        String username = request.getParameter("username");

        log.warn("Failed login attempt from IP: {} for user: {}", ipAddress, username);

        // Record failed attempt
        recordFailedAttempt(ipAddress);

        // Check if IP is blocked
        if (isBlocked(ipAddress)) {
            AttemptInfo attemptInfo = attemptsCache.get(ipAddress);
            long remainingMinutes = getRemainingLockoutTime(attemptInfo);

            log.warn("Blocked login attempt from IP: {} (blocked for {} more minutes)", ipAddress, remainingMinutes);

            // Set error message in session
            request.getSession().setAttribute("SPRING_SECURITY_LAST_EXCEPTION",
                    "Too many failed login attempts. Your IP is temporarily blocked. Please try again in "
                            + remainingMinutes + " minutes.");

            // Redirect to login page with error
            getRedirectStrategy().sendRedirect(request, response, "/admin/login?error=blocked");
            return;
        }

        // Get remaining attempts
        int remainingAttempts = getRemainingAttempts(ipAddress);

        // Set error message with remaining attempts
        String errorMessage = exception.getMessage();
        if (remainingAttempts > 0) {
            errorMessage += " (Remaining attempts: " + remainingAttempts + ")";
        }

        request.getSession().setAttribute("SPRING_SECURITY_LAST_EXCEPTION", errorMessage);

        // Redirect to login page with error
        getRedirectStrategy().sendRedirect(request, response, "/admin/login?error=invalid");
    }

    /**
     * Record a failed login attempt for the given IP address.
     * 
     * @param ipAddress the IP address
     */
    public void recordFailedAttempt(String ipAddress) {
        attemptsCache.compute(ipAddress, (key, attemptInfo) -> {
            if (attemptInfo == null) {
                return new AttemptInfo(1, System.currentTimeMillis());
            }

            // If lockout has expired, reset counter
            if (isLockoutExpired(attemptInfo)) {
                return new AttemptInfo(1, System.currentTimeMillis());
            }

            // Increment counter
            return new AttemptInfo(attemptInfo.attempts + 1, attemptInfo.lastAttemptTime);
        });
    }

    /**
     * Reset failed attempts for the given IP address (called on successful login).
     * 
     * @param ipAddress the IP address
     */
    public void resetFailedAttempts(String ipAddress) {
        attemptsCache.remove(ipAddress);
        log.debug("Reset failed attempts for IP: {}", ipAddress);
    }

    /**
     * Check if the IP address is currently blocked.
     * 
     * @param ipAddress the IP address
     * @return true if blocked, false otherwise
     */
    public boolean isBlocked(String ipAddress) {
        AttemptInfo attemptInfo = attemptsCache.get(ipAddress);
        if (attemptInfo == null) {
            return false;
        }

        // If lockout has expired, remove from cache
        if (isLockoutExpired(attemptInfo)) {
            attemptsCache.remove(ipAddress);
            return false;
        }

        return attemptInfo.attempts >= MAX_ATTEMPTS;
    }

    /**
     * Get remaining attempts before lockout.
     * 
     * @param ipAddress the IP address
     * @return number of remaining attempts
     */
    public int getRemainingAttempts(String ipAddress) {
        AttemptInfo attemptInfo = attemptsCache.get(ipAddress);
        if (attemptInfo == null || isLockoutExpired(attemptInfo)) {
            return MAX_ATTEMPTS;
        }
        return Math.max(0, MAX_ATTEMPTS - attemptInfo.attempts);
    }

    /**
     * Get remaining lockout time in minutes for a specific IP.
     * 
     * @param ipAddress the IP address
     * @return remaining minutes of lockout, or 0 if not blocked
     */
    public long getRemainingLockoutMinutes(String ipAddress) {
        AttemptInfo attemptInfo = attemptsCache.get(ipAddress);
        if (attemptInfo == null || !isBlocked(ipAddress)) {
            return 0;
        }
        return getRemainingLockoutTime(attemptInfo);
    }

    /**
     * Get remaining lockout time in minutes.
     * 
     * @param attemptInfo the attempt information
     * @return remaining minutes of lockout
     */
    private long getRemainingLockoutTime(AttemptInfo attemptInfo) {
        long elapsedTime = System.currentTimeMillis() - attemptInfo.lastAttemptTime;
        long remainingTime = TimeUnit.MINUTES.toMillis(LOCKOUT_DURATION_MINUTES) - elapsedTime;
        return TimeUnit.MILLISECONDS.toMinutes(remainingTime) + 1; // Add 1 to round up
    }

    /**
     * Check if the lockout period has expired.
     * 
     * @param attemptInfo the attempt information
     * @return true if lockout has expired, false otherwise
     */
    private boolean isLockoutExpired(AttemptInfo attemptInfo) {
        long elapsedTime = System.currentTimeMillis() - attemptInfo.lastAttemptTime;
        return elapsedTime > TimeUnit.MINUTES.toMillis(LOCKOUT_DURATION_MINUTES);
    }

    /**
     * Get the client's IP address from the request.
     * 
     * Checks X-Forwarded-For header first (for proxies/load balancers),
     * then falls back to remote address.
     * 
     * @param request the HTTP request
     * @return the client IP address
     */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        // X-Forwarded-For can contain multiple IPs, take the first one
        return xfHeader.split(",")[0].trim();
    }

    /**
     * Inner class to store attempt information.
     */
    private static class AttemptInfo {
        private final int attempts;
        private final long lastAttemptTime;

        public AttemptInfo(int attempts, long lastAttemptTime) {
            this.attempts = attempts;
            this.lastAttemptTime = lastAttemptTime;
        }
    }

    /**
     * Get statistics about current rate limiting state (for monitoring/admin).
     * 
     * @return map of statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTrackedIPs", attemptsCache.size());
        stats.put("maxAttempts", MAX_ATTEMPTS);
        stats.put("lockoutDurationMinutes", LOCKOUT_DURATION_MINUTES);

        long blockedCount = attemptsCache.values().stream()
                .filter(info -> info.attempts >= MAX_ATTEMPTS && !isLockoutExpired(info))
                .count();
        stats.put("currentlyBlocked", blockedCount);

        return stats;
    }
}
