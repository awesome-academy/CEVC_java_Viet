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
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ModelAndView handleDuplicateResourceException(
            DuplicateResourceException ex, HttpServletRequest request) {

        logger.warn("Duplicate resource: {}", ex.getMessage());

        ModelAndView mav = new ModelAndView("error/400");
        mav.addObject("errorMessage", ex.getMessage());
        mav.addObject("requestUrl", request.getRequestURI());
        mav.setStatus(HttpStatus.CONFLICT);

        return mav;
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
