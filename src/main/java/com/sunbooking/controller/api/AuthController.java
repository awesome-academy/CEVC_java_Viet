package com.sunbooking.controller.api;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunbooking.dto.api.request.LoginRequest;
import com.sunbooking.dto.api.request.RegisterRequest;
import com.sunbooking.dto.api.response.ApiResponse;
import com.sunbooking.dto.api.response.AuthResponse;
import com.sunbooking.entity.User;
import com.sunbooking.entity.UserRole;
import com.sunbooking.repository.UserRepository;
import com.sunbooking.security.CustomUserDetails;
import com.sunbooking.security.jwt.JwtService;

/**
 * REST controller for authentication endpoints.
 * Handles user registration and login with JWT token generation.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MessageSource messageSource;

    /**
     * Register a new user.
     *
     * @param registerRequest registration details
     * @param request         HTTP request for locale
     * @return API response with registration result
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody RegisterRequest registerRequest,
            HttpServletRequest request) {

        logger.info("Registration attempt for email: {}", registerRequest.getEmail());
        Locale locale = request.getLocale();

        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            logger.warn("Registration failed: Email already exists - {}", registerRequest.getEmail());
            String errorMsg = messageSource.getMessage("api.auth.register.email.exists", null, locale);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(errorMsg));
        }

        // Create new user
        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setPhone(registerRequest.getPhone());
        user.setRole(UserRole.USER);
        user.setIsActive(true);

        userRepository.save(user);

        logger.info("User registered successfully: {}", user.getEmail());
        String successMsg = messageSource.getMessage("api.auth.register.success", null, locale);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(successMsg, null));
    }

    /**
     * Authenticate user and generate JWT token.
     *
     * @param loginRequest login credentials
     * @param request      HTTP request for locale
     * @return API response with JWT token and user details
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {

        logger.info("Login attempt for email: {}", loginRequest.getEmail());
        Locale locale = request.getLocale();

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String jwt = jwtService.generateToken(userDetails);

            // Build response
            AuthResponse authResponse = new AuthResponse();
            authResponse.setToken(jwt);
            authResponse.setType("Bearer");
            authResponse.setId(userDetails.getUser().getId());
            authResponse.setEmail(userDetails.getUsername());
            authResponse.setName(userDetails.getUser().getName());
            authResponse.setRole(userDetails.getUser().getRole().toString());

            logger.info("Login successful for user: {}", loginRequest.getEmail());
            String successMsg = messageSource.getMessage("api.auth.login.success", null, locale);

            return ResponseEntity.ok(ApiResponse.success(successMsg, authResponse));

        } catch (BadCredentialsException e) {
            logger.warn("Login failed: Invalid credentials for email: {}", loginRequest.getEmail());
            String errorMsg = messageSource.getMessage("api.auth.login.invalid", null, locale);
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(errorMsg));
        } catch (Exception e) {
            logger.error("Login error for email: {}", loginRequest.getEmail(), e);
            String errorMsg = messageSource.getMessage("api.auth.login.error", null, locale);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(errorMsg));
        }
    }
}
