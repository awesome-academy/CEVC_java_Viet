package com.sunbooking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Base Security Configuration for Sun Booking Tour Application.
 * 
 * This configuration provides:
 * - Password encoding with BCrypt (strength 12)
 * - Authentication manager bean
 * - Global method security annotations
 * 
 * Additional security configurations for Admin (session-based) and API
 * (JWT-based) are in AdminSecurityConfig and ApiSecurityConfig.
 * 
 * @author Sun Booking Team
 * @version 1.0
 * @since 2025-11-12
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityBeansConfig {

    /**
     * Password encoder bean using BCrypt hashing algorithm.
     * 
     * BCrypt is a strong, adaptive password hashing function that is resistant to
     * brute-force attacks. The strength parameter (12) determines the number of
     * hashing rounds - higher values are more secure but slower.
     * 
     * Strength recommendations:
     * - 10-12: Standard security (suitable for most applications)
     * - 13-15: High security (recommended for sensitive data)
     * - 16+: Very high security (may impact performance)
     * 
     * @return BCryptPasswordEncoder with strength 12
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Strength 12 provides good balance between security and performance
        // Each increment doubles the time required to hash a password
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Authentication manager bean for handling authentication requests.
     * 
     * The AuthenticationManager is the main interface for authentication in Spring
     * Security.
     * It processes Authentication objects and returns a fully authenticated object
     * if successful.
     * 
     * This bean will be used by:
     * - Admin session-based authentication (Task 3.2)
     * - API JWT-based authentication (Task 3.3)
     * - Custom authentication providers
     * 
     * @param config the authentication configuration
     * @return AuthenticationManager instance
     * @throws Exception if authentication manager cannot be created
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
