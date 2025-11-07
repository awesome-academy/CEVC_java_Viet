package com.sunbooking.exception;

/**
 * Exception thrown when business logic rules are violated
 */
public class BusinessLogicException extends RuntimeException {

    private String errorCode;

    public BusinessLogicException(String message) {
        super(message);
    }

    public BusinessLogicException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessLogicException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getErrorCode() {
        return errorCode;
    }
}
