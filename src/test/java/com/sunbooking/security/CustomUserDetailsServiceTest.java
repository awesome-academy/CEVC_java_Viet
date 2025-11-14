package com.sunbooking.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.sunbooking.entity.User;
import com.sunbooking.entity.UserRole;
import com.sunbooking.repository.UserRepository;

/**
 * Unit tests for CustomUserDetailsService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService Tests")
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private User activeAdminUser;
    private User inactiveUser;

    @BeforeEach
    void setUp() {
        // Create active admin user
        activeAdminUser = new User();
        activeAdminUser.setId(1L);
        activeAdminUser.setName("Admin User");
        activeAdminUser.setEmail("admin@sunbooking.com");
        activeAdminUser.setPassword("$2a$12$hashed_password");
        activeAdminUser.setRole(UserRole.ADMIN);
        activeAdminUser.setIsActive(true);

        // Create inactive user
        inactiveUser = new User();
        inactiveUser.setId(2L);
        inactiveUser.setName("Inactive User");
        inactiveUser.setEmail("inactive@sunbooking.com");
        inactiveUser.setPassword("$2a$12$hashed_password");
        inactiveUser.setRole(UserRole.USER);
        inactiveUser.setIsActive(false);
    }

    @Test
    @DisplayName("Should load user by username successfully")
    void testLoadUserByUsername_Success() {
        // Given
        when(userRepository.findByEmail("admin@sunbooking.com"))
                .thenReturn(Optional.of(activeAdminUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("admin@sunbooking.com");

        // Then
        assertNotNull(userDetails);
        assertEquals("admin@sunbooking.com", userDetails.getUsername());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        verify(userRepository).findByEmail("admin@sunbooking.com");
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testLoadUserByUsername_UserNotFound() {
        // Given
        when(userRepository.findByEmail("notfound@sunbooking.com"))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("notfound@sunbooking.com");
        });
        verify(userRepository).findByEmail("notfound@sunbooking.com");
    }

    @Test
    @DisplayName("Should throw exception when user is inactive")
    void testLoadUserByUsername_InactiveUser() {
        // Given
        when(userRepository.findByEmail("inactive@sunbooking.com"))
                .thenReturn(Optional.of(inactiveUser));

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("inactive@sunbooking.com");
        });
        verify(userRepository).findByEmail("inactive@sunbooking.com");
    }

    @Test
    @DisplayName("Should load user by ID successfully")
    void testLoadUserById_Success() {
        // Given
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(activeAdminUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserById(1L);

        // Then
        assertNotNull(userDetails);
        assertEquals("admin@sunbooking.com", userDetails.getUsername());
        assertTrue(userDetails.isEnabled());
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when user ID not found")
    void testLoadUserById_UserNotFound() {
        // Given
        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserById(999L);
        });
        verify(userRepository).findById(999L);
    }

    @Test
    @DisplayName("Should return CustomUserDetails with correct authorities")
    void testLoadUserByUsername_CheckAuthorities() {
        // Given
        when(userRepository.findByEmail("admin@sunbooking.com"))
                .thenReturn(Optional.of(activeAdminUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("admin@sunbooking.com");

        // Then
        assertNotNull(userDetails);
        assertTrue(userDetails instanceof CustomUserDetails);
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }
}
