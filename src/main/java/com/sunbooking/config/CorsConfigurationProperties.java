package com.sunbooking.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Configuration properties for CORS settings.
 * Binds properties from application.properties with prefix "cors".
 */
@Configuration
@ConfigurationProperties(prefix = "cors")
@Data
public class CorsConfigurationProperties {

    /**
     * List of allowed origins for CORS requests.
     * Example: http://localhost:3000,http://localhost:4200
     */
    private List<String> allowedOrigins;

    /**
     * List of allowed HTTP methods.
     * Example: GET,POST,PUT,DELETE,PATCH,OPTIONS
     */
    private List<String> allowedMethods;

    /**
     * List of allowed headers.
     * Example: Authorization,Content-Type,Accept
     * Use "*" for all headers (not recommended in production)
     */
    private List<String> allowedHeaders;

    /**
     * List of headers exposed to the client.
     * Example: Authorization,X-Total-Count
     */
    private List<String> exposedHeaders;

    /**
     * Whether credentials (cookies, authorization headers) are allowed.
     * Default: true
     */
    private Boolean allowCredentials = true;

    /**
     * How long (in seconds) the preflight response can be cached.
     * Default: 3600 (1 hour)
     */
    private Long maxAge = 3600L;
}
