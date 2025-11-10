package com.sunbooking.exception;

import org.springframework.http.HttpStatus;

/**
 * Enumeration of standardized error codes for the application
 */
public enum ErrorCode {

    // 4xx Client Errors
    VALIDATION_ERROR("VALIDATION_ERROR", "Validation Error", HttpStatus.BAD_REQUEST),
    BUSINESS_LOGIC_ERROR("BUSINESS_LOGIC_ERROR", "Business Logic Error", HttpStatus.BAD_REQUEST),
    INVALID_INPUT("INVALID_INPUT", "Invalid Input", HttpStatus.BAD_REQUEST),

    UNAUTHORIZED("UNAUTHORIZED", "Unauthorized", HttpStatus.UNAUTHORIZED),

    ACCESS_DENIED("ACCESS_DENIED", "Access Denied", HttpStatus.FORBIDDEN),

    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "Resource Not Found", HttpStatus.NOT_FOUND),
    ENDPOINT_NOT_FOUND("ENDPOINT_NOT_FOUND", "Endpoint Not Found", HttpStatus.NOT_FOUND),

    DUPLICATE_RESOURCE("DUPLICATE_RESOURCE", "Resource Already Exists", HttpStatus.CONFLICT),

    // 5xx Server Errors
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR),

    EXTERNAL_SERVICE_ERROR("EXTERNAL_SERVICE_ERROR", "External Service Error", HttpStatus.BAD_GATEWAY);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public int getStatusValue() {
        return httpStatus.value();
    }
}
