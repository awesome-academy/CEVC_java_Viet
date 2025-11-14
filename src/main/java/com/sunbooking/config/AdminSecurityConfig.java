package com.sunbooking.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.sunbooking.security.CustomAuthenticationFailureHandler;
import com.sunbooking.security.CustomAuthenticationSuccessHandler;
import com.sunbooking.security.CustomUserDetailsService;

import lombok.extern.slf4j.Slf4j;

/**
 * Admin Security Configuration for session-based authentication.
 * 
 * This configuration handles authentication and authorization for the admin
 * site (/admin/**).
 * It provides:
 * - Session-based authentication with custom login page
 * - CSRF protection enabled
 * - Remember-me functionality with persistent token store
 * - Rate limiting to prevent brute force attacks
 * - Session management (timeout, concurrent sessions)
 * - Role-based access control (ADMIN role required)
 * 
 * This configuration has @Order(1) to be processed before API security config
 * (Task 3.3).
 * 
 * @author Sun Booking Team
 * @version 1.0
 * @since 2025-11-12
 */
@Configuration
@EnableWebSecurity
@Order(1)
@Slf4j
public class AdminSecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final CustomAuthenticationSuccessHandler authenticationSuccessHandler;
    private final CustomAuthenticationFailureHandler authenticationFailureHandler;
    private final DataSource dataSource;

    public AdminSecurityConfig(CustomUserDetailsService userDetailsService,
            CustomAuthenticationSuccessHandler authenticationSuccessHandler,
            CustomAuthenticationFailureHandler authenticationFailureHandler,
            DataSource dataSource) {
        this.userDetailsService = userDetailsService;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.dataSource = dataSource;
    }

    /**
     * Configure security filter chain for admin site.
     * 
     * @param http the HttpSecurity to configure
     * @return SecurityFilterChain for admin site
     * @throws Exception if configuration fails
     */
    @Bean
    @Order(1)
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring admin security filter chain");

        http
                // Apply this configuration only to /admin/** paths
                .antMatcher("/admin/**")

                // Authorization rules
                .authorizeHttpRequests()
                // Allow access to login page and static resources
                .antMatchers("/admin/login", "/admin/css/**", "/admin/js/**", "/admin/images/**",
                        "/admin/plugins/**")
                .permitAll()
                // All other /admin/** endpoints require ADMIN role
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()

                // Form login configuration
                .formLogin()
                .loginPage("/admin/login") // Custom login page URL
                .loginProcessingUrl("/admin/login") // URL to submit the login form
                .usernameParameter("username") // Form parameter for username (email)
                .passwordParameter("password") // Form parameter for password
                .successHandler(authenticationSuccessHandler) // Custom success handler
                .failureHandler(authenticationFailureHandler) // Custom failure handler with i18n and rate limiting
                .permitAll()
                .and()

                // Logout configuration
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/admin/logout", "POST"))
                .logoutSuccessUrl("/admin/login?logout")
                .invalidateHttpSession(true) // Invalidate session on logout
                .deleteCookies("JSESSIONID", "remember-me") // Delete cookies
                .permitAll()
                .and()

                // Remember-me configuration
                .rememberMe()
                .key("sunbooking-remember-me-key") // Secret key for token generation
                .tokenRepository(persistentTokenRepository()) // Persistent token store
                .userDetailsService(userDetailsService) // User details service
                .tokenValiditySeconds(14 * 24 * 60 * 60) // 14 days
                .rememberMeParameter("remember-me") // Form parameter name
                .rememberMeCookieName("remember-me")
                .and()

                // Session management
                .sessionManagement()
                .maximumSessions(1) // Only one session per user
                .maxSessionsPreventsLogin(false) // New login invalidates old session
                .expiredUrl("/admin/login?expired")
                .and()
                .and()

                // CSRF protection enabled (default for session-based auth)
                .csrf()
                .ignoringAntMatchers("/admin/login"); // Allow POST to login without CSRF token initially

        log.info("Admin security filter chain configured successfully");

        return http.build();
    }

    /**
     * Configure persistent token repository for remember-me functionality.
     * 
     * This stores remember-me tokens in the database for security and
     * persistence across server restarts.
     * 
     * @return PersistentTokenRepository backed by database
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        // Set create table on startup to false since Flyway handles schema
        tokenRepository.setCreateTableOnStartup(false);
        return tokenRepository;
    }
}
