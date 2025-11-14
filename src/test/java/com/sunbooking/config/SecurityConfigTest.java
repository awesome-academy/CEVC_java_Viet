package com.sunbooking.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test class for SecurityConfig.
 * 
 * Verifies that the base security configuration is properly initialized with:
 * - Password encoder bean
 * - Authentication manager bean
 * - Security filter chain
 * 
 * @author Sun Booking Team
 * @version 1.0
 * @since 2025-11-12
 */
@SpringBootTest
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Test that the password encoder bean is created and configured correctly.
     */
    @Test
    void testPasswordEncoderBeanExists() {
        assertNotNull(passwordEncoder, "PasswordEncoder bean should be created");
    }

    /**
     * Test that BCrypt password encoding works correctly.
     */
    @Test
    void testPasswordEncodingWorks() {
        String rawPassword = "testPassword123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertNotNull(encodedPassword, "Encoded password should not be null");
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword),
                "Password should match after encoding");
        assertTrue(encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$"),
                "Encoded password should use BCrypt format");
    }

    /**
     * Test that different password encodings produce different hashes.
     */
    @Test
    void testPasswordEncodingProducesDifferentHashes() {
        String rawPassword = "testPassword123";
        String encoded1 = passwordEncoder.encode(rawPassword);
        String encoded2 = passwordEncoder.encode(rawPassword);

        assertTrue(!encoded1.equals(encoded2),
                "Same password should produce different hashes due to salt");
        assertTrue(passwordEncoder.matches(rawPassword, encoded1),
                "First hash should match original password");
        assertTrue(passwordEncoder.matches(rawPassword, encoded2),
                "Second hash should match original password");
    }

    /**
     * Test that wrong passwords don't match.
     */
    @Test
    void testWrongPasswordDoesNotMatch() {
        String rawPassword = "correctPassword";
        String wrongPassword = "wrongPassword";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertTrue(!passwordEncoder.matches(wrongPassword, encodedPassword),
                "Wrong password should not match");
    }

    /**
     * Test that the authentication manager bean is created.
     */
    @Test
    void testAuthenticationManagerBeanExists() {
        assertNotNull(authenticationManager, "AuthenticationManager bean should be created");
    }

    /**
     * Test that the security filter chain bean exists.
     */
    @Test
    void testSecurityFilterChainBeanExists() {
        assertTrue(applicationContext.containsBean("filterChain"),
                "SecurityFilterChain bean should be created");
    }

    /**
     * Test that global method security is enabled.
     */
    @Test
    void testGlobalMethodSecurityEnabled() {
        // Verify that the @EnableGlobalMethodSecurity annotation is working
        // by checking that method security interceptor is configured
        String[] beanNames = applicationContext.getBeanNamesForType(
                org.springframework.security.access.intercept.aopalliance.MethodSecurityInterceptor.class);
        assertTrue(beanNames.length > 0,
                "Method security should be enabled");
    }
}
