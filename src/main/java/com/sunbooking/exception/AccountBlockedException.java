package com.sunbooking.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Exception thrown when an account is temporarily blocked due to too many
 * failed login attempts.
 * 
 * This exception is used by the rate limiting mechanism to signal that
 * login attempts from a specific IP address should be denied for a period of
 * time.
 * 
 * @author Sun Booking Team
 * @version 1.0
 * @since 2025-11-13
 */
public class AccountBlockedException extends AuthenticationException {

    private final long remainingMinutes;

    /**
     * Constructs a new AccountBlockedException with the specified message.
     * 
     * @param msg the detail message
     */
    public AccountBlockedException(String msg) {
        super(msg);
        this.remainingMinutes = 0;
    }

    /**
     * Constructs a new AccountBlockedException with the specified message and
     * remaining lockout time.
     * 
     * @param msg              the detail message
     * @param remainingMinutes the remaining lockout time in minutes
     */
    public AccountBlockedException(String msg, long remainingMinutes) {
        super(msg);
        this.remainingMinutes = remainingMinutes;
    }

    /**
     * Constructs a new AccountBlockedException with the specified message and
     * cause.
     * 
     * @param msg   the detail message
     * @param cause the cause of the exception
     */
    public AccountBlockedException(String msg, Throwable cause) {
        super(msg, cause);
        this.remainingMinutes = 0;
    }

    /**
     * Get the remaining lockout time in minutes.
     * 
     * @return the remaining lockout time in minutes
     */
    public long getRemainingMinutes() {
        return remainingMinutes;
    }
}
