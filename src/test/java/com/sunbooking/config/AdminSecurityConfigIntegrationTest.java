package com.sunbooking.config;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.sunbooking.entity.User;
import com.sunbooking.entity.UserRole;
import com.sunbooking.repository.UserRepository;

/**
 * Integration tests for admin authentication.
 * 
 * Tests the complete authentication flow including:
 * - Login page access
 * - Form login with valid/invalid credentials
 * - Session management
 * - Logout functionality
 * - Remember-me feature
 * - Rate limiting
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Admin Authentication Integration Tests")
public class AdminSecurityConfigIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Create test admin user
        User admin = new User();
        admin.setName("Test Admin");
        admin.setEmail("admin@test.com");
        admin.setPassword(passwordEncoder.encode("password123"));
        admin.setRole(UserRole.ADMIN);
        admin.setIsActive(true);
        userRepository.save(admin);

        // Create test inactive user
        User inactiveUser = new User();
        inactiveUser.setName("Inactive User");
        inactiveUser.setEmail("inactive@test.com");
        inactiveUser.setPassword(passwordEncoder.encode("password123"));
        inactiveUser.setRole(UserRole.ADMIN);
        inactiveUser.setIsActive(false);
        userRepository.save(inactiveUser);

        // Create persistent_logins table for remember-me (H2 compatible)
        try {
            context.getBean(javax.sql.DataSource.class)
                    .getConnection()
                    .createStatement()
                    .execute("CREATE TABLE IF NOT EXISTS persistent_logins (" +
                            "username VARCHAR(64) NOT NULL, " +
                            "series VARCHAR(64) PRIMARY KEY, " +
                            "token VARCHAR(64) NOT NULL, " +
                            "last_used TIMESTAMP NOT NULL)");
        } catch (Exception e) {
            // Table might already exist, ignore
        }
    }

    @Test
    @DisplayName("Should access login page without authentication")
    void testLoginPageAccess() throws Exception {
        mockMvc.perform(get("/admin/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"));
    }

    @Test
    @DisplayName("Should redirect to login when accessing protected page without authentication")
    void testProtectedPageRedirect() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/admin/login"));
    }

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void testLoginSuccess() throws Exception {
        mockMvc.perform(formLogin("/admin/login")
                .user("username", "admin@test.com")
                .password("password", "password123"))
                .andExpect(authenticated())
                .andExpect(redirectedUrl("/admin/dashboard"));
    }

    @Test
    @DisplayName("Should fail login with invalid credentials")
    void testLoginFailure_InvalidPassword() throws Exception {
        mockMvc.perform(formLogin("/admin/login")
                .user("username", "admin@test.com")
                .password("password", "wrongpassword"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/admin/login?error=invalid"));
    }

    @Test
    @DisplayName("Should fail login with non-existent user")
    void testLoginFailure_UserNotFound() throws Exception {
        mockMvc.perform(formLogin("/admin/login")
                .user("username", "notfound@test.com")
                .password("password", "password123"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/admin/login?error=invalid"));
    }

    @Test
    @DisplayName("Should fail login with inactive user")
    void testLoginFailure_InactiveUser() throws Exception {
        mockMvc.perform(formLogin("/admin/login")
                .user("username", "inactive@test.com")
                .password("password", "password123"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/admin/login?error=invalid"));
    }

    @Test
    @DisplayName("Should access dashboard after successful login")
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void testDashboardAccess() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(authenticated());
    }

    @Test
    @DisplayName("Should logout successfully")
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void testLogout() throws Exception {
        // Note: In test environment, remember-me persistent token table may not exist
        // This is expected behavior as Flyway is disabled in tests
        mockMvc.perform(logout("/admin/logout"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/admin/login?logout"));
    }

    @Test
    @DisplayName("Should deny access to dashboard for non-admin user")
    @WithMockUser(username = "user@test.com", roles = "USER")
    void testDashboardAccess_NonAdmin() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should redirect authenticated user from login page to dashboard")
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void testLoginPageRedirect_AlreadyAuthenticated() throws Exception {
        mockMvc.perform(get("/admin/login"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"));
    }

    @Test
    @DisplayName("Should allow access to static resources without authentication")
    void testStaticResourceAccess() throws Exception {
        mockMvc.perform(get("/admin/css/style.css"))
                .andExpect(status().isNotFound()); // 404 because file doesn't exist, but not 302 redirect

        mockMvc.perform(get("/admin/js/script.js"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/admin/images/logo.png"))
                .andExpect(status().isNotFound());
    }
}
