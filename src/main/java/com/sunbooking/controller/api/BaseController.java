package com.sunbooking.controller.api;

import org.springframework.web.bind.annotation.RestController;

import com.sunbooking.swagger.CommonResponses;

/**
 * Base controller providing common Swagger documentation and configurations
 * for all API controllers.
 */
@RestController
@CommonResponses.InternalServerError
public abstract class BaseController {

    /**
     * Base API controller with common error responses.
     * All controllers extending this will inherit:
     * - 500 Internal Server Error response documentation
     * - Common exception handling (when implemented)
     * - Consistent API response structure
     */

    // Common controller functionality can be added here:
    // - Common exception handlers
    // - Common validation methods
    // - Common response builders
    // - Logging utilities
}