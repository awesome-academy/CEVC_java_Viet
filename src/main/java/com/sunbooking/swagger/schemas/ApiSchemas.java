package com.sunbooking.swagger.schemas;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Reusable Swagger schemas for API documentation.
 */
public class ApiSchemas {

    /**
     * Standard API response wrapper schema.
     */
    @Schema(description = "Standard API response wrapper")
    public static class ApiResponseSchema {

        @Schema(description = "Indicates if the request was successful", example = "true")
        public Boolean success;

        @Schema(description = "Human-readable message describing the result", example = "Operation completed successfully")
        public String message;

        @Schema(description = "Response data (null if error)")
        public Object data;
    }

    /**
     * User data response schema (without sensitive information).
     */
    @Schema(description = "User information response")
    public static class UserDataSchema {

        @Schema(description = "User unique identifier", example = "1")
        public Long id;

        @Schema(description = "User's full name", example = "John Doe")
        public String name;

        @Schema(description = "User's email address", example = "john.doe@example.com")
        public String email;

        @Schema(description = "User's phone number", example = "0123456789")
        public String phone;

        @Schema(description = "Account creation timestamp", example = "2024-01-15T10:30:00")
        public String createdAt;
    }

    /**
     * Authentication response schema with JWT token.
     */
    @Schema(description = "Authentication response with JWT token")
    public static class AuthResponseSchema {

        @Schema(description = "JWT authentication token", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjM5...")
        public String token;

        @Schema(description = "User information")
        public UserDataSchema user;
    }

    /**
     * Validation error response schema.
     */
    @Schema(description = "Validation error response")
    public static class ValidationErrorSchema {

        @Schema(description = "Always false for error responses", example = "false")
        public Boolean success;

        @Schema(description = "Error message", example = "One or more fields have validation errors")
        public String message;

        @Schema(description = "Field-specific validation errors")
        public Object errors;
    }

    /**
     * Rate limiting error schema.
     */
    @Schema(description = "Rate limiting error response")
    public static class RateLimitErrorSchema {

        @Schema(description = "Always false for error responses", example = "false")
        public Boolean success;

        @Schema(description = "Rate limit error message", example = "Too many failed login attempts. Your IP is temporarily blocked. Please try again in 15 minutes")
        public String message;

        @Schema(description = "Always null for error responses")
        public Object data;
    }
}