package com.sunbooking.exception;

/**
 * Exception thrown when business logic rules are violated
 */
public class BusinessLogicException extends RuntimeException {

    private final String errorCode;

    public BusinessLogicException(String message) {
        super(message);
        this.errorCode = null;
    }

    public BusinessLogicException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessLogicException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
    }

    public BusinessLogicException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
