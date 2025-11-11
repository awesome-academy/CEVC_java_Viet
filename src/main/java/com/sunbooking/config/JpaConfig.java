package com.sunbooking.config;

import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for JPA settings.
 * Note: JPA Auditing is already enabled in SunBookingTourApplication class
 * through @EnableJpaAuditing annotation, which allows automatic population
 * of @CreatedDate and @LastModifiedDate fields.
 */
@Configuration
public class JpaConfig {
    // JPA configuration can be added here if needed in the future
    // Current JPA Auditing is enabled in the main application class
}
