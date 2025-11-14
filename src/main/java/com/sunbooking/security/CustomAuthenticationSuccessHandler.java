package com.sunbooking.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Custom authentication success handler.
 * 
 * This handler is called after successful authentication. It performs:
 * - Reset failed login attempts for the IP address
 * - Log successful authentication
 * - Redirect to the originally requested page (or default page)
 * 
 * @author Sun Booking Team
 * @version 1.0
 * @since 2025-11-12
 */
@Component
@Slf4j
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final LoginAttemptService loginAttemptService;

    public CustomAuthenticationSuccessHandler(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
        // Set default target URL for successful login
        setDefaultTargetUrl("/admin/dashboard");
        // Always use default target URL if no saved request exists
        setAlwaysUseDefaultTargetUrl(false);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        String ipAddress = getClientIP(request);
        String username = authentication.getName();

        log.info("Successful login from IP: {} for user: {}", ipAddress, username);

        // Reset failed attempts for this IP
        loginAttemptService.resetFailedAttempts(ipAddress);

        // Call parent handler to perform redirect
        super.onAuthenticationSuccess(request, response, authentication);
    }

    /**
     * Get the client's IP address from the request.
     * 
     * @param request the HTTP request
     * @return the client IP address
     */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
