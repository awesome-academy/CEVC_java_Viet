package com.sunbooking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for Swagger UI and OpenAPI documentation.
 * Allows public access to API documentation endpoints.
 */
@Configuration
@Order(0) // Highest priority to be processed before other security configurations
public class SwaggerSecurityConfig {

    /**
     * Configure security filter chain for Swagger endpoints.
     * Allows public access to API documentation.
     *
     * @param http the HttpSecurity to configure
     * @return SecurityFilterChain for Swagger endpoints
     * @throws Exception if configuration fails
     */
    @Bean
    @Order(0)
    public SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Apply to Swagger endpoints only
                .requestMatcher(request -> {
                    String uri = request.getRequestURI();
                    return uri.startsWith("/swagger-ui") ||
                            uri.equals("/swagger-ui.html") ||
                            uri.startsWith("/v3/api-docs") ||
                            uri.startsWith("/webjars");
                })

                // Disable CSRF for documentation endpoints
                .csrf().disable()

                // Allow public access to all Swagger endpoints
                .authorizeHttpRequests()
                .antMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/webjars/**")
                .permitAll()
                .anyRequest().permitAll()
                .and()

                .build();
    }
}