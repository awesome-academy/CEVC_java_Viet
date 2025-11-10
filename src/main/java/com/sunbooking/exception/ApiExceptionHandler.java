package com.sunbooking.exception;

import java.nio.file.AccessDeniedException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.sunbooking.dto.response.ErrorResponse;

/**
 * Global exception handler for REST API endpoints
 * Handles all exceptions thrown by API controllers and returns appropriate
 * error responses
 */
@RestControllerAdvice(basePackages = "com.sunbooking.controller.api")
public class ApiExceptionHandler {

        private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

        /**
         * Handle ResourceNotFoundException
         */
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
                        ResourceNotFoundException ex, HttpServletRequest request) {

                logger.warn("Resource not found: {}", ex.getMessage());

                ErrorCode errorCode = ErrorCode.RESOURCE_NOT_FOUND;
                ErrorResponse errorResponse = new ErrorResponse(
                                errorCode.getStatusValue(),
                                errorCode.getCode(),
                                errorCode.getMessage(),
                                ex.getMessage(),
                                request.getRequestURI());

                return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
        }

        /**
         * Handle ValidationException
         */
        @ExceptionHandler(ValidationException.class)
        public ResponseEntity<ErrorResponse> handleValidationException(
                        ValidationException ex, HttpServletRequest request) {

                logger.warn("Validation error: {}", ex.getMessage());

                ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
                ErrorResponse errorResponse = new ErrorResponse(
                                errorCode.getStatusValue(),
                                errorCode.getCode(),
                                errorCode.getMessage(),
                                ex.getMessage(),
                                request.getRequestURI());

                if (ex.getField() != null) {
                        errorResponse.addValidationError(ex.getField(), ex.getMessage());
                }

                return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
        }

        /**
         * Handle UnauthorizedException
         */
        @ExceptionHandler(UnauthorizedException.class)
        public ResponseEntity<ErrorResponse> handleUnauthorizedException(
                        UnauthorizedException ex, HttpServletRequest request) {

                logger.warn("Unauthorized access: {}", ex.getMessage());

                ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
                ErrorResponse errorResponse = new ErrorResponse(
                                errorCode.getStatusValue(),
                                errorCode.getCode(),
                                errorCode.getMessage(),
                                ex.getMessage(),
                                request.getRequestURI());

                return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
        }

        /**
         * Handle BusinessLogicException
         */
        @ExceptionHandler(BusinessLogicException.class)
        public ResponseEntity<ErrorResponse> handleBusinessLogicException(
                        BusinessLogicException ex, HttpServletRequest request) {

                logger.warn("Business logic error: {}", ex.getMessage());

                ErrorCode errorCode = ErrorCode.BUSINESS_LOGIC_ERROR;
                ErrorResponse errorResponse = new ErrorResponse(
                                errorCode.getStatusValue(),
                                errorCode.getCode(),
                                errorCode.getMessage(),
                                ex.getMessage(),
                                request.getRequestURI());

                return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
        }

        /**
         * Handle DuplicateResourceException
         */
        @ExceptionHandler(DuplicateResourceException.class)
        public ResponseEntity<ErrorResponse> handleDuplicateResourceException(
                        DuplicateResourceException ex, HttpServletRequest request) {

                logger.warn("Duplicate resource: {}", ex.getMessage());

                ErrorCode errorCode = ErrorCode.DUPLICATE_RESOURCE;
                ErrorResponse errorResponse = new ErrorResponse(
                                errorCode.getStatusValue(),
                                errorCode.getCode(),
                                errorCode.getMessage(),
                                ex.getMessage(),
                                request.getRequestURI());

                return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
        }

        /**
         * Handle MethodArgumentNotValidException (Bean Validation)
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
                        MethodArgumentNotValidException ex, HttpServletRequest request) {

                logger.warn("Validation failed: {}", ex.getMessage());

                ErrorCode errorCode = ErrorCode.INVALID_INPUT;
                ErrorResponse errorResponse = new ErrorResponse(
                                errorCode.getStatusValue(),
                                errorCode.getCode(),
                                errorCode.getMessage(),
                                "One or more fields have validation errors",
                                request.getRequestURI());

                // Add all validation errors
                ex.getBindingResult().getAllErrors().forEach(error -> {
                        String fieldName = ((FieldError) error).getField();
                        String errorMessage = error.getDefaultMessage();
                        errorResponse.addValidationError(fieldName, errorMessage);
                });

                return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
        }

        /**
         * Handle AccessDeniedException
         */
        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDeniedException(
                        AccessDeniedException ex, HttpServletRequest request) {

                logger.warn("Access denied: {}", ex.getMessage());

                ErrorCode errorCode = ErrorCode.ACCESS_DENIED;
                ErrorResponse errorResponse = new ErrorResponse(
                                errorCode.getStatusValue(),
                                errorCode.getCode(),
                                errorCode.getMessage(),
                                "You don't have permission to access this resource",
                                request.getRequestURI());

                return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
        }

        /**
         * Handle NoHandlerFoundException (404)
         */
        @ExceptionHandler(NoHandlerFoundException.class)
        public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(
                        NoHandlerFoundException ex, HttpServletRequest request) {

                logger.warn("No handler found for: {} {}", ex.getHttpMethod(), ex.getRequestURL());

                ErrorCode errorCode = ErrorCode.ENDPOINT_NOT_FOUND;
                ErrorResponse errorResponse = new ErrorResponse(
                                errorCode.getStatusValue(),
                                errorCode.getCode(),
                                errorCode.getMessage(),
                                "The requested endpoint does not exist",
                                request.getRequestURI());

                return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
        }

        /**
         * Handle all other exceptions
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGlobalException(
                        Exception ex, HttpServletRequest request) {

                logger.error("Unexpected error occurred: ", ex);

                ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
                ErrorResponse errorResponse = new ErrorResponse(
                                errorCode.getStatusValue(),
                                errorCode.getCode(),
                                errorCode.getMessage(),
                                "An unexpected error occurred. Please try again later.",
                                request.getRequestURI());

                return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
        }
}
