package com.sunbooking.security;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.sunbooking.exception.AccountBlockedException;

import lombok.extern.slf4j.Slf4j;

/**
 * Custom authentication failure handler with i18n support.
 * 
 * This handler processes authentication failures and provides localized
 * error messages using Spring's MessageSource. It works in conjunction with
 * LoginAttemptService for rate limiting.
 * 
 * Features:
 * - Internationalized error messages
 * - Different error types with specific messages
 * - Integration with rate limiting
 * - Detailed logging for security auditing
 * 
 * @author Sun Booking Team
 * @version 1.0
 * @since 2025-11-13
 */
@Component
@Slf4j
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final LoginAttemptService loginAttemptService;
    private final MessageSource messageSource;

    public CustomAuthenticationFailureHandler(LoginAttemptService loginAttemptService,
            MessageSource messageSource) {
        this.loginAttemptService = loginAttemptService;
        this.messageSource = messageSource;
        setDefaultFailureUrl("/admin/login?error=true");
    }

    /**
     * Handle authentication failure with i18n error messages.
     * 
     * @param request   the HTTP request
     * @param response  the HTTP response
     * @param exception the authentication exception
     * @throws IOException      if an I/O error occurs
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        String ipAddress = getClientIP(request);
        String username = request.getParameter("username");
        Locale locale = request.getLocale();

        log.warn("Failed login attempt from IP: {} for user: {}", ipAddress, username);

        // Record failed attempt
        loginAttemptService.recordFailedAttempt(ipAddress);

        // Check if IP is blocked
        if (loginAttemptService.isBlocked(ipAddress)) {
            handleBlockedAttempt(request, response, ipAddress, locale);
            return;
        }

        // Handle different types of authentication failures
        String errorMessage = getLocalizedErrorMessage(exception, ipAddress, locale);
        String errorType = getErrorType(exception);

        // Store error message in session
        request.getSession().setAttribute("authenticationError", errorMessage);

        log.debug("Authentication failed with error type: {} - {}", errorType, errorMessage);

        // Redirect to login page with error parameter
        getRedirectStrategy().sendRedirect(request, response, "/admin/login?error=" + errorType);
    }

    /**
     * Handle blocked login attempt.
     * 
     * @param request   the HTTP request
     * @param response  the HTTP response
     * @param ipAddress the blocked IP address
     * @param locale    the user's locale
     * @throws IOException if redirect fails
     */
    private void handleBlockedAttempt(HttpServletRequest request, HttpServletResponse response,
            String ipAddress, Locale locale) throws IOException {

        long remainingMinutes = loginAttemptService.getRemainingLockoutMinutes(ipAddress);

        log.warn("Blocked login attempt from IP: {} (blocked for {} more minutes)",
                ipAddress, remainingMinutes);

        // Get localized blocked message with remaining time
        String errorMessage = messageSource.getMessage(
                "login.error.blocked",
                new Object[] { remainingMinutes },
                locale);

        request.getSession().setAttribute("authenticationError", errorMessage);

        getRedirectStrategy().sendRedirect(request, response, "/admin/login?error=blocked");
    }

    /**
     * Get localized error message based on exception type.
     * 
     * @param exception the authentication exception
     * @param ipAddress the IP address
     * @param locale    the user's locale
     * @return localized error message
     */
    private String getLocalizedErrorMessage(AuthenticationException exception,
            String ipAddress, Locale locale) {

        // Account blocked by rate limiting
        if (exception instanceof AccountBlockedException) {
            AccountBlockedException blockedException = (AccountBlockedException) exception;
            return messageSource.getMessage(
                    "login.error.blocked",
                    new Object[] { blockedException.getRemainingMinutes() },
                    locale);
        }

        // Account disabled
        if (exception instanceof DisabledException) {
            return messageSource.getMessage("login.error.disabled", null, locale);
        }

        // Account locked
        if (exception instanceof LockedException) {
            return messageSource.getMessage("login.error.inactive", null, locale);
        }

        // User not found or bad credentials
        if (exception instanceof UsernameNotFoundException ||
                exception instanceof BadCredentialsException) {

            int remainingAttempts = loginAttemptService.getRemainingAttempts(ipAddress);

            // If there are remaining attempts, show them
            if (remainingAttempts > 0) {
                return messageSource.getMessage(
                        "login.error.credentials",
                        new Object[] { remainingAttempts },
                        locale);
            } else {
                return messageSource.getMessage("login.error.invalid", null, locale);
            }
        }

        // Generic authentication failure
        return messageSource.getMessage("login.error.general", null, locale);
    }

    /**
     * Get error type string for URL parameter.
     * 
     * @param exception the authentication exception
     * @return error type string
     */
    private String getErrorType(AuthenticationException exception) {
        if (exception instanceof AccountBlockedException) {
            return "blocked";
        }
        if (exception instanceof DisabledException) {
            return "disabled";
        }
        if (exception instanceof LockedException) {
            return "inactive";
        }
        if (exception instanceof UsernameNotFoundException ||
                exception instanceof BadCredentialsException) {
            return "invalid";
        }
        return "general";
    }

    /**
     * Get the client's IP address from the request.
     * 
     * Checks X-Forwarded-For header first (for proxies/load balancers),
     * then falls back to remote address.
     * 
     * @param request the HTTP request
     * @return the client IP address
     */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        // X-Forwarded-For can contain multiple IPs, take the first one
        return xfHeader.split(",")[0].trim();
    }
}
