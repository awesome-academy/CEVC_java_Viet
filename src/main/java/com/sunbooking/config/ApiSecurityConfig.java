package com.sunbooking.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.sunbooking.security.jwt.JwtAuthenticationEntryPoint;
import com.sunbooking.security.jwt.JwtAuthenticationFilter;

/**
 * Security configuration for API endpoints (/api/**).
 * Configures JWT-based stateless authentication with CORS support.
 */
@Configuration
@EnableWebSecurity
@Order(2) // Lower priority than AdminSecurityConfig (Order 1)
public class ApiSecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(ApiSecurityConfig.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private CorsConfigurationProperties corsConfigurationProperties;

    @Autowired
    private ApiSecurityProperties apiSecurityProperties;

    /**
     * Configure security filter chain for API endpoints.
     *
     * @param http the HttpSecurity to configure
     * @return SecurityFilterChain for API endpoints
     * @throws Exception if configuration fails
     */
    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring API security filter chain");

        http
                // Apply to API endpoints only
                .antMatcher("/api/**")

                // CORS configuration
                .cors().and()

                // CSRF not needed for stateless JWT authentication
                .csrf().disable()

                // Exception handling
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()

                // Stateless session management (no session creation)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                // Authorization rules
                .authorizeHttpRequests()
                // Public guest endpoints (from configuration)
                .antMatchers(apiSecurityProperties.getPublicEndpoints().toArray(new String[0])).permitAll()

                // Protected user endpoints (from configuration) - require authentication
                .antMatchers(apiSecurityProperties.getProtectedEndpoints().toArray(new String[0])).authenticated()

                // All other API requests require authentication
                .anyRequest().authenticated()
                .and()

                // Set authentication provider
                .authenticationProvider(authenticationProvider());

        // Add JWT filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        logger.info("API security filter chain configured successfully");
        logger.debug("Public endpoints: {}", apiSecurityProperties.getPublicEndpoints());
        logger.debug("Protected endpoints: {}", apiSecurityProperties.getProtectedEndpoints());

        return http.build();
    }

    /**
     * Authentication provider for API endpoints.
     *
     * @return DaoAuthenticationProvider configured with UserDetailsService and
     *         PasswordEncoder
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    /**
     * Configure CORS for API endpoints using configuration properties.
     *
     * @return CORS configuration source
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Set allowed origins from properties
        configuration.setAllowedOrigins(corsConfigurationProperties.getAllowedOrigins());

        // Set allowed methods from properties
        configuration.setAllowedMethods(corsConfigurationProperties.getAllowedMethods());

        // Set allowed headers from properties
        configuration.setAllowedHeaders(corsConfigurationProperties.getAllowedHeaders());

        // Set exposed headers from properties
        configuration.setExposedHeaders(corsConfigurationProperties.getExposedHeaders());

        // Set allow credentials from properties
        configuration.setAllowCredentials(corsConfigurationProperties.getAllowCredentials());

        // Set max age from properties
        configuration.setMaxAge(corsConfigurationProperties.getMaxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);

        logger.info("CORS configured for API endpoints");
        logger.debug("Allowed origins: {}", corsConfigurationProperties.getAllowedOrigins());
        logger.debug("Allowed methods: {}", corsConfigurationProperties.getAllowedMethods());

        return source;
    }
}
