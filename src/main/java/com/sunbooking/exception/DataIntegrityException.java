package com.sunbooking.exception;

/**
 * Exception thrown when data integrity constraints are violated.
 * Typically used when required relationships or fields are missing.
 */
public class DataIntegrityException extends RuntimeException {

    public DataIntegrityException(String message) {
        super(message);
    }

    public DataIntegrityException(String message, Throwable cause) {
        super(message, cause);
    }
}
