package com.sunbooking.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Configuration;

/**
 * Configuration for API Security settings.
 * Defines public and protected endpoint patterns for API access control.
 */
@Configuration
public class ApiSecurityProperties {

    /**
     * List of public endpoint patterns that don't require authentication.
     * Supports Ant-style path patterns (e.g., /api/tours/**).
     */
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/api/auth/register",
            "/api/auth/login",
            "/api/tours/**",
            "/api/reviews/**",
            "/api/categories/**");

    /**
     * List of protected endpoint patterns that require authentication.
     * Supports Ant-style path patterns (e.g., /api/profile/**).
     */
    private static final List<String> PROTECTED_ENDPOINTS = Arrays.asList(
            "/api/profile/**",
            "/api/bookings/**",
            "/api/comments/**",
            "/api/likes/**");

    /**
     * Get list of public endpoints.
     *
     * @return list of public endpoint patterns
     */
    public List<String> getPublicEndpoints() {
        return PUBLIC_ENDPOINTS;
    }

    /**
     * Get list of protected endpoints.
     *
     * @return list of protected endpoint patterns
     */
    public List<String> getProtectedEndpoints() {
        return PROTECTED_ENDPOINTS;
    }
}
