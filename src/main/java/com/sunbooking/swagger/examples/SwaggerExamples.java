package com.sunbooking.swagger.examples;

/**
 * Reusable JSON examples for Swagger documentation.
 */
public class SwaggerExamples {

    // Registration Examples
    public static final String REGISTRATION_REQUEST_EXAMPLE = "{\n" +
            "    \"name\": \"John Doe\",\n" +
            "    \"email\": \"john.doe@example.com\",\n" +
            "    \"password\": \"SecurePass123\",\n" +
            "    \"phone\": \"0123456789\"\n" +
            "}";

    public static final String REGISTRATION_SUCCESS_EXAMPLE = "{\n" +
            "    \"success\": true,\n" +
            "    \"message\": \"User registered successfully\",\n" +
            "    \"data\": {\n" +
            "        \"id\": 1,\n" +
            "        \"name\": \"John Doe\",\n" +
            "        \"email\": \"john.doe@example.com\",\n" +
            "        \"phone\": \"0123456789\",\n" +
            "        \"createdAt\": \"2024-01-15T10:30:00\"\n" +
            "    }\n" +
            "}";

    public static final String EMAIL_EXISTS_ERROR_EXAMPLE = "{\n" +
            "    \"success\": false,\n" +
            "    \"message\": \"Email already registered\",\n" +
            "    \"data\": null\n" +
            "}";

    public static final String RATE_LIMIT_ERROR_EXAMPLE = "{\n" +
            "    \"success\": false,\n" +
            "    \"message\": \"Too many failed login attempts. Your IP is temporarily blocked. Please try again in 15 minutes\",\n"
            +
            "    \"data\": null\n" +
            "}";

    // Login Examples
    public static final String LOGIN_REQUEST_EXAMPLE = "{\n" +
            "    \"email\": \"john.doe@example.com\",\n" +
            "    \"password\": \"SecurePass123\"\n" +
            "}";

    public static final String LOGIN_SUCCESS_EXAMPLE = "{\n" +
            "    \"success\": true,\n" +
            "    \"message\": \"Login successful\",\n" +
            "    \"data\": {\n" +
            "        \"token\": \"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjM5...\",\n" +
            "        \"user\": {\n" +
            "            \"id\": 1,\n" +
            "            \"name\": \"John Doe\",\n" +
            "            \"email\": \"john.doe@example.com\",\n" +
            "            \"phone\": \"0123456789\"\n" +
            "        }\n" +
            "    }\n" +
            "}";

    public static final String LOGIN_INVALID_CREDENTIALS_EXAMPLE = "{\n" +
            "    \"success\": false,\n" +
            "    \"message\": \"Invalid email or password\",\n" +
            "    \"data\": null\n" +
            "}";

    // Validation Examples
    public static final String VALIDATION_ERROR_EXAMPLE = "{\n" +
            "    \"success\": false,\n" +
            "    \"message\": \"One or more fields have validation errors\",\n" +
            "    \"errors\": {\n" +
            "        \"name\": \"Name is required\",\n" +
            "        \"email\": \"Email must be valid\",\n" +
            "        \"password\": \"Password must be between 6 and 255 characters\"\n" +
            "    }\n" +
            "}";

    // Generic Examples
    public static final String INTERNAL_SERVER_ERROR_EXAMPLE = "{\n" +
            "    \"success\": false,\n" +
            "    \"message\": \"An unexpected error occurred. Please try again later\",\n" +
            "    \"data\": null\n" +
            "}";

    public static final String UNAUTHORIZED_ERROR_EXAMPLE = "{\n" +
            "    \"success\": false,\n" +
            "    \"message\": \"You are not authorized to perform this action\",\n" +
            "    \"data\": null\n" +
            "}";
}