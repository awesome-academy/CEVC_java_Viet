package com.sunbooking.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.sunbooking.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Custom UserDetails implementation that wraps the User entity.
 * 
 * This class adapts our User entity to Spring Security's UserDetails interface,
 * allowing Spring Security to authenticate and authorize users based on our
 * domain model.
 * 
 * @author Sun Booking Team
 * @version 1.0
 * @since 2025-11-12
 */
@Data
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convert user role to Spring Security authority
        // Prefix with "ROLE_" as per Spring Security convention
        String authority = "ROLE_" + user.getRole().name();
        return Collections.singletonList(new SimpleGrantedAuthority(authority));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        // Use email as username for authentication
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        // Our system doesn't have account expiration
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Our system doesn't have account locking (yet)
        // Could be implemented later for rate limiting
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Our system doesn't have credential expiration
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Account is enabled if user is active (not soft-deleted)
        return user.getIsActive();
    }

    /**
     * Get the underlying User entity.
     * 
     * @return the User entity
     */
    public User getUser() {
        return user;
    }

    /**
     * Get the user's ID.
     * 
     * @return the user ID
     */
    public Long getId() {
        return user.getId();
    }

    /**
     * Get the user's display name.
     * 
     * @return the user's name
     */
    public String getName() {
        return user.getName();
    }

    /**
     * Check if the user has admin role.
     * 
     * @return true if user is an admin, false otherwise
     */
    public boolean isAdmin() {
        return user.isAdmin();
    }
}
