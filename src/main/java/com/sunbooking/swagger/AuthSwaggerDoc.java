package com.sunbooking.swagger;

import com.sunbooking.dto.api.request.LoginRequest;
import com.sunbooking.dto.api.request.RegisterRequest;
import com.sunbooking.dto.api.response.ApiResponse;
import com.sunbooking.swagger.examples.SwaggerExamples;
import com.sunbooking.swagger.schemas.ApiSchemas;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Swagger documentation for Authentication endpoints.
 * Centralizes all authentication-related API documentation.
 */
public class AuthSwaggerDoc {

    /**
     * User Registration Documentation
     */
    @Operation(summary = "Register a new user", description = "Create a new user account with email, password, name, and phone number. "
            +
            "Returns user details upon successful registration. " +
            "Includes rate limiting (5 attempts per 15 minutes per IP) to prevent spam.", tags = { "Authentication" })
    @RequestBody(description = "User registration information", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegisterRequest.class), examples = @ExampleObject(name = "Registration Example", summary = "Sample user registration", description = "Complete registration payload with all required fields", value = SwaggerExamples.REGISTRATION_REQUEST_EXAMPLE)))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User registered successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(name = "Registration Success", summary = "Successful registration response", description = "Returns user data without sensitive information", value = SwaggerExamples.REGISTRATION_SUCCESS_EXAMPLE))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error or email already exists", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiSchemas.ValidationErrorSchema.class), examples = {
                    @ExampleObject(name = "Email Already Exists", summary = "Email already registered error", description = "Returned when trying to register with an existing email", value = SwaggerExamples.EMAIL_EXISTS_ERROR_EXAMPLE),
                    @ExampleObject(name = "Validation Errors", summary = "Field validation errors", description = "Returned when request fields fail validation", value = SwaggerExamples.VALIDATION_ERROR_EXAMPLE)
            })),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "429", description = "Too many registration attempts", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiSchemas.RateLimitErrorSchema.class), examples = @ExampleObject(name = "Rate Limited", summary = "Rate limit exceeded", description = "IP blocked due to too many failed attempts (5 attempts/15 minutes)", value = SwaggerExamples.RATE_LIMIT_ERROR_EXAMPLE))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(name = "Internal Server Error", summary = "Unexpected server error", description = "Generic server error response", value = SwaggerExamples.INTERNAL_SERVER_ERROR_EXAMPLE)))
    })
    public @interface RegisterEndpoint {
    }

    /**
     * User Login Documentation
     */
    @Operation(summary = "User login", description = "Authenticate user with email and password. " +
            "Returns JWT token and user details upon successful authentication. " +
            "Token should be used in Authorization header as 'Bearer {token}' for protected endpoints.", tags = {
                    "Authentication" })
    @RequestBody(description = "User login credentials", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginRequest.class), examples = @ExampleObject(name = "Login Example", summary = "Sample user login", description = "Login with email and password", value = SwaggerExamples.LOGIN_REQUEST_EXAMPLE)))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiSchemas.AuthResponseSchema.class), examples = @ExampleObject(name = "Login Success", summary = "Successful login response", description = "Returns JWT token and user information", value = SwaggerExamples.LOGIN_SUCCESS_EXAMPLE))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(name = "Invalid Credentials", summary = "Invalid email or password", description = "Returned when credentials are incorrect", value = SwaggerExamples.LOGIN_INVALID_CREDENTIALS_EXAMPLE))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiSchemas.ValidationErrorSchema.class), examples = @ExampleObject(name = "Validation Errors", summary = "Field validation errors", description = "Returned when request fields fail validation", value = SwaggerExamples.VALIDATION_ERROR_EXAMPLE))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(name = "Internal Server Error", summary = "Unexpected server error", description = "Generic server error response", value = SwaggerExamples.INTERNAL_SERVER_ERROR_EXAMPLE)))
    })
    public @interface LoginEndpoint {
    }
}