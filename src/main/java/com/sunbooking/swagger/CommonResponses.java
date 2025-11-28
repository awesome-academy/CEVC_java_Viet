package com.sunbooking.swagger;

import com.sunbooking.swagger.examples.SwaggerExamples;
import com.sunbooking.swagger.schemas.ApiSchemas;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * Common reusable Swagger response definitions.
 */
public class CommonResponses {

    /**
     * Standard 400 Bad Request response for validation errors.
     */
    @ApiResponse(responseCode = "400", description = "Validation error or bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiSchemas.ValidationErrorSchema.class), examples = @ExampleObject(name = "Validation Error", summary = "Field validation errors", description = "Returned when request fields fail validation", value = SwaggerExamples.VALIDATION_ERROR_EXAMPLE)))
    public @interface BadRequest {
    }

    /**
     * Standard 401 Unauthorized response.
     */
    @ApiResponse(responseCode = "401", description = "Authentication required or invalid credentials", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiSchemas.ApiResponseSchema.class), examples = @ExampleObject(name = "Unauthorized", summary = "Authentication failed", description = "Invalid or missing authentication credentials", value = SwaggerExamples.UNAUTHORIZED_ERROR_EXAMPLE)))
    public @interface Unauthorized {
    }

    /**
     * Standard 429 Too Many Requests response for rate limiting.
     */
    @ApiResponse(responseCode = "429", description = "Rate limit exceeded", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiSchemas.RateLimitErrorSchema.class), examples = @ExampleObject(name = "Rate Limited", summary = "Too many requests", description = "Request rate limit exceeded, try again later", value = SwaggerExamples.RATE_LIMIT_ERROR_EXAMPLE)))
    public @interface RateLimited {
    }

    /**
     * Standard 500 Internal Server Error response.
     */
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiSchemas.ApiResponseSchema.class), examples = @ExampleObject(name = "Internal Server Error", summary = "Unexpected server error", description = "An unexpected error occurred on the server", value = SwaggerExamples.INTERNAL_SERVER_ERROR_EXAMPLE)))
    public @interface InternalServerError {
    }
}