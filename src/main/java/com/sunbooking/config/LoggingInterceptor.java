package com.sunbooking.config;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor for logging HTTP requests and responses
 * Adds request ID to MDC for correlation across logs
 */
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);
    private static final String REQUEST_ID = "requestId";
    private static final String START_TIME = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Generate unique request ID
        String requestId = UUID.randomUUID().toString();
        MDC.put(REQUEST_ID, requestId);

        // Store start time
        request.setAttribute(START_TIME, System.currentTimeMillis());

        // Log incoming request
        logger.info("Incoming Request: {} {} from {} | User-Agent: {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr(),
                request.getHeader("User-Agent"));

        // Log query parameters if present
        if (request.getQueryString() != null) {
            logger.debug("Query Parameters: {}", request.getQueryString());
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) {
        // Calculate request duration
        Long startTime = (Long) request.getAttribute(START_TIME);
        long duration = 0;
        if (startTime != null) {
            duration = System.currentTimeMillis() - startTime;
        }

        // Log response
        logger.info("Outgoing Response: {} {} | Status: {} | Duration: {}ms",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                duration);

        // Log exception if present
        if (ex != null) {
            logger.error("Request failed with exception: ", ex);
        }

        // Warn on slow requests (>1 second)
        if (duration > 1000) {
            logger.warn("SLOW REQUEST DETECTED: {} {} took {}ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    duration);
        }

        // Clean up MDC
        MDC.remove(REQUEST_ID);
    }
}
