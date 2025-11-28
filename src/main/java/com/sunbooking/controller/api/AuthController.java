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
import com.sunbooking.dto.api.response.UserDTO;
import com.sunbooking.entity.User;
import com.sunbooking.entity.UserRole;
import com.sunbooking.repository.UserRepository;
import com.sunbooking.security.CustomUserDetails;
import com.sunbooking.security.LoginAttemptService;
import com.sunbooking.security.jwt.JwtService;
import com.sunbooking.swagger.AuthSwaggerDoc;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller for authentication endpoints.
 * Handles user registration and login with JWT token generation.
 * 
 * API documentation is centralized in AuthSwaggerDoc class for better
 * maintainability.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
public class AuthController extends BaseController {

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

    @Autowired
    private LoginAttemptService loginAttemptService;

    /**
     * Register a new user.
     *
     * @param registerRequest registration details
     * @param request         HTTP request for locale and IP tracking
     * @return API response with user data
     */
    @AuthSwaggerDoc.RegisterEndpoint
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(
            @Valid @RequestBody RegisterRequest registerRequest,
            HttpServletRequest request) {

        logger.info("Registration attempt for email: {}", registerRequest.getEmail());
        Locale locale = request.getLocale();

        // Check rate limiting
        String clientIP = getClientIP(request);

        if (loginAttemptService.isBlocked(clientIP)) {
            long remainingMinutes = loginAttemptService.getRemainingLockoutMinutes(clientIP);
            logger.warn("Registration blocked due to rate limiting from IP: {}, remaining: {} minutes",
                    clientIP, remainingMinutes);

            String errorMsg = messageSource.getMessage("login.error.blocked",
                    new Object[] { remainingMinutes }, locale);
            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(ApiResponse.error(errorMsg));
        }

        try {
            // Check if email already exists
            if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
                String errorMsg = messageSource.getMessage("api.auth.register.email.exists", null, locale);
                loginAttemptService.recordFailedAttempt(clientIP);
                logger.warn("Registration failed - email already exists: {}", registerRequest.getEmail());
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(ApiResponse.error(errorMsg));
            }

            // Create new user
            User user = new User();
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setName(registerRequest.getName());
            user.setRole(UserRole.USER);
            user.setIsActive(true);

            // Save user
            User savedUser = userRepository.save(user);
            logger.info("User registered successfully: {}", savedUser.getEmail());

            // Create UserDTO
            UserDTO userDTO = UserDTO.fromEntity(savedUser);

            String successMsg = messageSource.getMessage("api.auth.register.success", null, locale);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success(successMsg, userDTO));

        } catch (Exception e) {
            logger.error("Registration failed for email: {}", registerRequest.getEmail(), e);
            loginAttemptService.recordFailedAttempt(clientIP);
            String errorMsg = messageSource.getMessage("api.error.internal", null, locale);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(errorMsg));
        }
    }

    /**
     * Authenticate user and generate JWT token.
     *
     * @param loginRequest login credentials
     * @param request      HTTP request for locale and IP tracking
     * @return JWT token and user data
     */
    @AuthSwaggerDoc.LoginEndpoint
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {

        logger.info("Login attempt for email: {}", loginRequest.getEmail());

        // Check rate limiting
        String clientIP = getClientIP(request);

        if (loginAttemptService.isBlocked(clientIP)) {
            long remainingMinutes = loginAttemptService.getRemainingLockoutMinutes(clientIP);
            logger.warn("Login blocked due to rate limiting from IP: {}, remaining: {} minutes",
                    clientIP, remainingMinutes);

            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new AuthResponse(null, null));
        }

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user details
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            // Generate JWT token
            String token = jwtService.generateToken(userDetails);

            // Create UserDTO
            UserDTO userDTO = UserDTO.fromEntity(user);

            logger.info("User logged in successfully: {}", user.getEmail());

            // Reset failed attempts on successful login
            loginAttemptService.resetFailedAttempts(clientIP);

            return ResponseEntity.ok(new AuthResponse(token, userDTO));

        } catch (BadCredentialsException e) {
            logger.warn("Login failed - invalid credentials for email: {}", loginRequest.getEmail());
            loginAttemptService.recordFailedAttempt(clientIP);

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, null));
        } catch (Exception e) {
            logger.error("Login failed for email: {}", loginRequest.getEmail(), e);
            loginAttemptService.recordFailedAttempt(clientIP);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse(null, null));
        }
    }

    /**
     * Extract client IP address from request.
     *
     * @param request HTTP request
     * @return client IP address
     */
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }

        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty() && !"unknown".equalsIgnoreCase(xRealIP)) {
            return xRealIP;
        }

        return request.getRemoteAddr();
    }
}
