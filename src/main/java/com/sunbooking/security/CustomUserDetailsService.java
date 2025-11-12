package com.sunbooking.security;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sunbooking.entity.User;
import com.sunbooking.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Custom UserDetailsService implementation for loading user-specific data with
 * i18n support.
 * 
 * This service is used by Spring Security during the authentication process
 * to load user details from the database. It retrieves user information by
 * email and wraps it in a CustomUserDetails object. All exception messages
 * are internationalized using Spring's MessageSource.
 * 
 * @author Sun Booking Team
 * @version 1.0
 * @since 2025-11-12
 */
@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final MessageSource messageSource;

    public CustomUserDetailsService(UserRepository userRepository, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.messageSource = messageSource;
    }

    /**
     * Load user details by username (email in our case).
     * 
     * This method is called by Spring Security during authentication.
     * It retrieves the user from the database by email and returns a
     * UserDetails object that Spring Security can use for authentication
     * and authorization.
     * 
     * @param username the email address of the user (we use email as username)
     * @return UserDetails object containing user information and authorities
     * @throws UsernameNotFoundException if user with given email is not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);

        Locale locale = LocaleContextHolder.getLocale();

        // Find user by email (username is email in our system)
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", username);
                    String message = messageSource.getMessage(
                            "auth.user.not.found",
                            new Object[] { username },
                            locale);
                    return new UsernameNotFoundException(message);
                });

        // Check if user is active
        if (!user.getIsActive()) {
            log.warn("User account is inactive: {}", username);
            String message = messageSource.getMessage(
                    "auth.user.inactive",
                    new Object[] { username },
                    locale);
            throw new UsernameNotFoundException(message);
        }

        log.debug("User loaded successfully: {} (Role: {})", username, user.getRole());

        // Wrap user entity in CustomUserDetails
        return new CustomUserDetails(user);
    }

    /**
     * Load user details by user ID.
     * 
     * This is a convenience method for loading users by ID, which can be
     * useful for JWT authentication or other scenarios where we have the
     * user ID but not the email.
     * 
     * @param userId the ID of the user
     * @return UserDetails object containing user information and authorities
     * @throws UsernameNotFoundException if user with given ID is not found
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        log.debug("Loading user by ID: {}", userId);

        Locale locale = LocaleContextHolder.getLocale();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    String message = messageSource.getMessage(
                            "auth.user.not.found.by.id",
                            new Object[] { userId },
                            locale);
                    return new UsernameNotFoundException(message);
                });

        // Check if user is active
        if (!user.getIsActive()) {
            log.warn("User account is inactive (ID: {})", userId);
            String message = messageSource.getMessage(
                    "auth.user.inactive.by.id",
                    new Object[] { userId },
                    locale);
            throw new UsernameNotFoundException(message);
        }

        log.debug("User loaded successfully (ID: {}, Email: {}, Role: {})", userId, user.getEmail(), user.getRole());

        return new CustomUserDetails(user);
    }
}
