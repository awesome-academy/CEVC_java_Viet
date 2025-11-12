package com.sunbooking.controller.admin;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;

/**
 * Controller for admin authentication pages.
 * 
 * Handles login page rendering with i18n error messages and logout
 * confirmation.
 * Uses Spring's MessageSource for centralized, internationalized error
 * handling.
 * 
 * @author Sun Booking Team
 * @version 1.0
 * @since 2025-11-12
 */
@Controller
@RequestMapping("/admin")
@Slf4j
public class LoginController {

    private final MessageSource messageSource;

    public LoginController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Display admin login page.
     * 
     * If user is already authenticated, redirect to dashboard.
     * Otherwise, show login form with any error/success messages using i18n.
     * 
     * @param error   optional error parameter
     * @param logout  optional logout parameter
     * @param expired optional expired session parameter
     * @param model   the model to add attributes to
     * @param request the HTTP request
     * @return the login view name
     */
    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "expired", required = false) String expired,
            Model model,
            HttpServletRequest request) {

        log.debug("Admin login page requested");

        // Check if user is already authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            log.debug("User already authenticated, redirecting to dashboard");
            return "redirect:/admin/dashboard";
        }

        Locale locale = request.getLocale();
        HttpSession session = request.getSession(false);

        // Handle error messages from authentication failure handler
        if (error != null && session != null) {
            String errorMessage = (String) session.getAttribute("authenticationError");
            if (errorMessage != null) {
                model.addAttribute("error", errorMessage);
                session.removeAttribute("authenticationError");
                log.debug("Login error from session: {}", errorMessage);
            } else {
                // Fallback to generic error message if no specific message in session
                String genericError = messageSource.getMessage("login.error.general", null, locale);
                model.addAttribute("error", genericError);
                log.debug("Login error (generic): {}", genericError);
            }
        }

        // Handle logout message
        if (logout != null) {
            String logoutMessage = messageSource.getMessage("login.message.logout", null, locale);
            model.addAttribute("message", logoutMessage);
            log.debug("User logged out successfully");
        }

        // Handle session expired message
        if (expired != null) {
            String expiredMessage = messageSource.getMessage("login.error.expired", null, locale);
            model.addAttribute("error", expiredMessage);
            log.debug("Session expired");
        }

        return "admin/auth/login";
    }

    /**
     * Dashboard page (temporary for testing).
     * 
     * Will be replaced by proper dashboard implementation in Task 4.2.
     * 
     * @param model the model
     * @return the dashboard view name
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        log.debug("Dashboard accessed by user: {}", auth.getName());
        return "admin/dashboard";
    }
}
