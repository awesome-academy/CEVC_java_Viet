package com.sunbooking.exception;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Global exception handler for MVC/Admin controllers
 * Handles all exceptions thrown by admin controllers and returns appropriate
 * error views
 */
@ControllerAdvice(basePackages = "com.sunbooking.controller.admin")
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle ResourceNotFoundException
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {

        logger.warn("Resource not found: {}", ex.getMessage());

        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("errorMessage", ex.getMessage());
        mav.addObject("requestUrl", request.getRequestURI());
        mav.setStatus(HttpStatus.NOT_FOUND);

        return mav;
    }

    /**
     * Handle ValidationException
     */
    @ExceptionHandler(ValidationException.class)
    public ModelAndView handleValidationException(
            ValidationException ex, HttpServletRequest request) {

        logger.warn("Validation error: {}", ex.getMessage());

        ModelAndView mav = new ModelAndView("error/400");
        mav.addObject("errorMessage", ex.getMessage());
        mav.addObject("requestUrl", request.getRequestURI());
        mav.setStatus(HttpStatus.BAD_REQUEST);

        return mav;
    }

    /**
     * Handle UnauthorizedException
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ModelAndView handleUnauthorizedException(
            UnauthorizedException ex, HttpServletRequest request) {

        logger.warn("Unauthorized access: {}", ex.getMessage());

        ModelAndView mav = new ModelAndView("error/401");
        mav.addObject("errorMessage", ex.getMessage());
        mav.addObject("requestUrl", request.getRequestURI());
        mav.setStatus(HttpStatus.UNAUTHORIZED);

        return mav;
    }

    /**
     * Handle BusinessLogicException
     */
    @ExceptionHandler(BusinessLogicException.class)
    public ModelAndView handleBusinessLogicException(
            BusinessLogicException ex, HttpServletRequest request) {

        logger.warn("Business logic error: {}", ex.getMessage());

        ModelAndView mav = new ModelAndView("error/400");
        mav.addObject("errorMessage", ex.getMessage());
        mav.addObject("requestUrl", request.getRequestURI());
        mav.setStatus(HttpStatus.BAD_REQUEST);

        return mav;
    }

    /**
     * Handle DuplicateResourceException
     * For form submissions, return to form with error message
     * For other requests, show error page
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ModelAndView handleDuplicateResourceException(
            DuplicateResourceException ex, HttpServletRequest request) {

        logger.warn("Duplicate resource: {}", ex.getMessage());

        // If it's a POST request to an edit endpoint, return to the appropriate form
        // with error
        if ("POST".equals(request.getMethod()) && request.getRequestURI().contains("/edit")) {
            // Extract the resource type from the URI (e.g., /admin/users, /admin/tours)
            String uri = request.getRequestURI();
            String viewName = determineFormViewFromUri(uri);

            ModelAndView mav = new ModelAndView(viewName);
            mav.addObject("errorMessage", ex.getMessage());
            mav.addObject("isEdit", true);
            mav.setStatus(HttpStatus.CONFLICT);
            return mav;
        }

        // Otherwise, show error page
        ModelAndView mav = new ModelAndView("error/400");
        mav.addObject("errorMessage", ex.getMessage());
        mav.addObject("requestUrl", request.getRequestURI());
        mav.setStatus(HttpStatus.CONFLICT);

        return mav;
    }

    /**
     * Handle IllegalStateException
     * Used for business rule violations (e.g., cannot delete own account, last
     * admin)
     */
    @ExceptionHandler(IllegalStateException.class)
    public ModelAndView handleIllegalStateException(
            IllegalStateException ex, HttpServletRequest request) {

        logger.warn("Illegal state: {}", ex.getMessage());

        ModelAndView mav = new ModelAndView("error/400");
        mav.addObject("errorMessage", ex.getMessage());
        mav.addObject("requestUrl", request.getRequestURI());
        mav.setStatus(HttpStatus.BAD_REQUEST);

        return mav;
    }

    /**
     * Determine the form view name from the request URI
     * 
     * @param uri the request URI (e.g., /admin/users/123/edit)
     * @return the view name (e.g., admin/users/form)
     */
    private String determineFormViewFromUri(String uri) {
        // Extract resource path: /admin/{resource}/{id}/edit -> admin/{resource}/form
        if (uri.startsWith("/admin/")) {
            String[] parts = uri.split("/");
            if (parts.length >= 3) {
                // parts[0] = "", parts[1] = "admin", parts[2] = "users|tours|categories"
                String resource = parts[2];
                return "admin/" + resource + "/form";
            }
        }

        // Default fallback
        return "error/400";
    }

    /**
     * Handle AccessDeniedException
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {

        logger.warn("Access denied: {}", ex.getMessage());

        ModelAndView mav = new ModelAndView("error/403");
        mav.addObject("errorMessage", "You don't have permission to access this resource");
        mav.addObject("requestUrl", request.getRequestURI());
        mav.setStatus(HttpStatus.FORBIDDEN);

        return mav;
    }

    /**
     * Handle NoHandlerFoundException (404)
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpServletRequest request) {

        logger.warn("No handler found for: {} {}", ex.getHttpMethod(), ex.getRequestURL());

        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("errorMessage", "The requested page does not exist");
        mav.addObject("requestUrl", request.getRequestURI());
        mav.setStatus(HttpStatus.NOT_FOUND);

        return mav;
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGlobalException(
            Exception ex, HttpServletRequest request) {

        logger.error("Unexpected error occurred: ", ex);

        ModelAndView mav = new ModelAndView("error/500");
        mav.addObject("errorMessage", "An unexpected error occurred. Please try again later.");
        mav.addObject("requestUrl", request.getRequestURI());
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        return mav;
    }
}
