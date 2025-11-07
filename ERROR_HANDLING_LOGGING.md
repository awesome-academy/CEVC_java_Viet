# Error Handling & Logging Infrastructure

## Overview

Task 1.5 implements a comprehensive error handling and logging infrastructure for the Sun Booking Tour application. This includes custom exception classes, global exception handlers for both MVC and REST API, standardized error responses, and structured logging with request correlation.

---

## Exception Classes

### 1. ResourceNotFoundException

**Location**: `src/main/java/com/sunbooking/exception/ResourceNotFoundException.java`

**Purpose**: Thrown when a requested resource is not found in the database.

**Usage Example**:

```java
// By resource name and field
throw new ResourceNotFoundException("User", "id", userId);
// Output: "User not found with id : '123'"

// By custom message
throw new ResourceNotFoundException("Tour not found or has been deleted");
```

---

### 2. ValidationException

**Location**: `src/main/java/com/sunbooking/exception/ValidationException.java`

**Purpose**: Thrown when validation fails for business logic.

**Usage Example**:

```java
// With field information
throw new ValidationException("email", userEmail, "Email already exists");

// Simple message
throw new ValidationException("Price must be greater than 0");
```

---

### 3. UnauthorizedException

**Location**: `src/main/java/com/sunbooking/exception/UnauthorizedException.java`

**Purpose**: Thrown when a user is not authorized to perform an action.

**Usage Example**:

```java
throw new UnauthorizedException("You must be logged in to access this resource");
throw new UnauthorizedException("Invalid or expired token");
```

---

### 4. BusinessLogicException

**Location**: `src/main/java/com/sunbooking/exception/BusinessLogicException.java`

**Purpose**: Thrown when business logic rules are violated.

**Usage Example**:

```java
// With error code
throw new BusinessLogicException("BOOKING_001", "Cannot cancel booking after tour has started");

// Simple message
throw new BusinessLogicException("Cannot delete category with active tours");
```

---

### 5. DuplicateResourceException

**Location**: `src/main/java/com/sunbooking/exception/DuplicateResourceException.java`

**Purpose**: Thrown when a duplicate resource is attempted to be created.

**Usage Example**:

```java
// By resource name and field
throw new DuplicateResourceException("User", "email", email);
// Output: "User already exists with email : 'user@example.com'"

// Custom message
throw new DuplicateResourceException("Category name must be unique");
```

---

## Exception Handlers

### 1. ApiExceptionHandler (REST API)

**Location**: `src/main/java/com/sunbooking/exception/ApiExceptionHandler.java`

**Scope**: All controllers in `com.sunbooking.controller.api` package

**Features**:

- Handles all custom exceptions
- Handles Spring validation errors (`@Valid`)
- Returns standardized JSON error responses
- Logs all errors with appropriate levels
- Includes request path in error response

**Response Format**:

```json
{
  "timestamp": "2025-11-07T16:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id : '123'",
  "path": "/api/users/123"
}
```

**Validation Error Response**:

```json
{
  "timestamp": "2025-11-07T16:00:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "One or more fields have validation errors",
  "path": "/api/users/register",
  "validationErrors": [
    {
      "field": "email",
      "message": "must be a well-formed email address"
    },
    {
      "field": "password",
      "message": "size must be between 8 and 100"
    }
  ]
}
```

---

### 2. GlobalExceptionHandler (MVC/Admin)

**Location**: `src/main/java/com/sunbooking/exception/GlobalExceptionHandler.java`

**Scope**: All controllers in `com.sunbooking.controller.admin` package

**Features**:

- Handles all custom exceptions
- Returns appropriate error views (404.html, 400.html, 401.html, 403.html, 500.html)
- Passes error information to Thymeleaf templates
- Logs all errors with appropriate levels

**Error View Variables**:

- `errorMessage`: User-friendly error message
- `requestUrl`: The URL that caused the error

---

## Standard Response DTOs

### 1. ApiResponse<T>

**Location**: `src/main/java/com/sunbooking/dto/response/ApiResponse.java`

**Purpose**: Standard wrapper for all successful API responses

**Usage Example**:

```java
// Success with data
return ResponseEntity.ok(ApiResponse.success(userData));

// Success with custom message
return ResponseEntity.ok(ApiResponse.success("User created successfully", userData));

// Success with message only
return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));

// Error response
return ResponseEntity.badRequest().body(ApiResponse.error("Invalid input"));

// Error with multiple messages
return ResponseEntity.badRequest().body(
    ApiResponse.error("Validation failed", Arrays.asList("Email required", "Password too short"))
);
```

**Response Format**:

```json
{
  "success": true,
  "message": "Success",
  "data": {
    "id": 1,
    "name": "John Doe"
  },
  "timestamp": "2025-11-07T16:00:00"
}
```

---

### 2. ErrorResponse

**Location**: `src/main/java/com/sunbooking/dto/response/ErrorResponse.java`

**Purpose**: Standard error response for REST API

**Features**:

- Timestamp of error
- HTTP status code
- Error type
- Error message
- Request path
- Validation errors (if applicable)

---

## Logging Infrastructure

### 1. LoggingInterceptor

**Location**: `src/main/java/com/sunbooking/config/LoggingInterceptor.java`

**Features**:

- Generates unique request ID for each request
- Adds request ID to MDC (Mapped Diagnostic Context) for log correlation
- Logs incoming requests with:
  - HTTP method
  - Request URI
  - Remote IP address
  - User-Agent
  - Query parameters
- Logs outgoing responses with:
  - HTTP status code
  - Request duration
- Warns on slow requests (>1 second)
- Logs exceptions if request fails

**Log Output Example**:

```
2025-11-07 16:00:00.123 [http-nio-8080-exec-1] [a1b2c3d4-5678-90ef-ghij-klmnopqrstuv] INFO  c.s.c.LoggingInterceptor - Incoming Request: GET /api/tours from 127.0.0.1 | User-Agent: Mozilla/5.0
2025-11-07 16:00:00.456 [http-nio-8080-exec-1] [a1b2c3d4-5678-90ef-ghij-klmnopqrstuv] INFO  c.s.c.LoggingInterceptor - Outgoing Response: GET /api/tours | Status: 200 | Duration: 333ms
```

---

### 2. Logback Configuration

**Location**: `src/main/resources/logback-spring.xml`

**Features**:

- **Console Appender**: Colorized console output with request ID
- **File Appender**: Application logs with request ID
- **Error File Appender**: Separate file for ERROR level logs
- **Rolling Policy**:
  - Dev: Max 100MB per file, 7-day retention
  - Prod: Max 1GB per file, 30-day retention
- **Profile-specific log levels**:
  - Dev: DEBUG level, verbose SQL logging
  - Prod: WARN level, minimal logging
  - Test: INFO level, moderate logging

**Log Pattern**:

```
%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] [%X{requestId:-NO_REQUEST_ID}] %-5level %logger{36} - %msg%n
```

**Request ID Correlation**: All logs within the same request share the same `requestId`, making it easy to trace the full lifecycle of a request.

---

### 3. Application Properties Configuration

**Location**: `src/main/resources/application.properties`

**Logging Configuration**:

```properties
# Logging Pattern with Request ID
logging.pattern.console=%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(%5p) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr([%X{requestId:-NO_REQUEST_ID}]){blue} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n%wEx
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] [%X{requestId:-NO_REQUEST_ID}] %-5level %logger{36} - %msg%n

# Error Handling
spring.mvc.throw-exception-if-no-handler-found=true
spring.web.resources.add-mappings=false
server.error.include-message=always
server.error.include-binding-errors=always
server.error.include-stacktrace=on_param
server.error.include-exception=false
```

---

### 4. WebMvcConfig

**Location**: `src/main/java/com/sunbooking/config/WebMvcConfig.java`

**Purpose**: Registers the LoggingInterceptor for all requests

**Features**:

- Intercepts all requests except static resources (CSS, JS, images)
- Automatically adds request ID to logs
- Measures request duration

---

## HTTP Status Code Mapping

| Exception                       | HTTP Status               | Use Case                           |
| ------------------------------- | ------------------------- | ---------------------------------- |
| ResourceNotFoundException       | 404 Not Found             | Resource doesn't exist in database |
| ValidationException             | 400 Bad Request           | Input validation failed            |
| UnauthorizedException           | 401 Unauthorized          | User not authenticated             |
| AccessDeniedException           | 403 Forbidden             | User lacks permissions             |
| DuplicateResourceException      | 409 Conflict              | Resource already exists            |
| BusinessLogicException          | 400 Bad Request           | Business rule violation            |
| MethodArgumentNotValidException | 400 Bad Request           | Bean validation failed (@Valid)    |
| NoHandlerFoundException         | 404 Not Found             | Endpoint doesn't exist             |
| Exception (all others)          | 500 Internal Server Error | Unexpected errors                  |

---

## Usage Guidelines

### For Service Layer

```java
@Service
public class UserService {

    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public User createUser(UserDTO dto) {
        // Check for duplicate email
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("User", "email", dto.getEmail());
        }

        // Business logic validation
        if (dto.getAge() < 18) {
            throw new ValidationException("age", dto.getAge(), "User must be at least 18 years old");
        }

        // Save user
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);

        // Business logic check
        if (user.hasActiveBookings()) {
            throw new BusinessLogicException("Cannot delete user with active bookings");
        }

        userRepository.delete(user);
    }
}
```

---

### For API Controllers

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUser(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@Valid @RequestBody CreateUserRequest request) {
        // @Valid automatically triggers MethodArgumentNotValidException if validation fails
        UserDTO user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("User created successfully", user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
    }
}
```

---

### For Admin Controllers (MVC)

```java
@Controller
@RequestMapping("/admin/users")
public class UserManagementController {

    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        // Exception will be caught by GlobalExceptionHandler and render error view
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "admin/users/view";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully");
        } catch (BusinessLogicException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }
}
```

---

## Log Levels by Environment

### Development (dev profile)

- **Application**: DEBUG
- **SQL Queries**: DEBUG
- **SQL Parameters**: TRACE
- **Spring Framework**: INFO
- **Spring Security**: DEBUG

### Production (prod profile)

- **Application**: WARN
- **SQL Queries**: WARN (no logging)
- **Spring Framework**: WARN
- **Spring Security**: WARN

### Test (test profile)

- **Application**: INFO
- **SQL Queries**: INFO
- **Spring Framework**: INFO
- **Spring Security**: INFO

---

## Security Event Logging (Preparation)

The infrastructure is prepared for security event logging. Future security implementations should log:

- **Authentication Events**:

  - Login success/failure
  - Logout
  - Token generation/validation
  - Password changes

- **Authorization Events**:

  - Access denied
  - Role changes
  - Permission violations

- **Suspicious Activities**:
  - Multiple failed login attempts
  - Invalid token usage
  - Unusual access patterns

**Example** (to be implemented in security modules):

```java
logger.warn("Failed login attempt for email: {} from IP: {}", email, request.getRemoteAddr());
logger.info("User {} successfully logged in from IP: {}", user.getEmail(), request.getRemoteAddr());
logger.error("Suspicious activity detected: {} failed login attempts for email: {}", attempts, email);
```

---

## Testing Error Handling

### Test Custom Exceptions

```java
@Test
void testResourceNotFound() {
    assertThrows(ResourceNotFoundException.class, () -> {
        userService.getUserById(999L);
    });
}

@Test
void testDuplicateResource() {
    // Create first user
    userService.createUser(userDTO);

    // Attempt to create duplicate
    assertThrows(DuplicateResourceException.class, () -> {
        userService.createUser(userDTO);
    });
}
```

### Test API Error Responses

```java
@Test
void testApiErrorResponse() throws Exception {
    mockMvc.perform(get("/api/users/999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message").value("User not found with id : '999'"))
        .andExpect(jsonPath("$.path").value("/api/users/999"));
}
```

---

## Log File Locations

Based on the active profile:

**Development Profile (dev)**:

- **Application Log**: `logs/sun-booking-tour-dev.log`
- **Error Log**: `logs/error.log`

**Production Profile (prod)**:

- **Application Log**: `logs/sun-booking-tour-prod.log`
- **Error Log**: `logs/error.log`

**Test Profile (test)**:

- **Application Log**: `logs/sun-booking-tour-test.log`
- **Error Log**: `logs/error.log`

Files are automatically rotated based on size and time retention policies.

---

## Summary

Task 1.5 provides a robust error handling and logging infrastructure:

✅ **5 Custom Exception Classes**: Cover all common error scenarios  
✅ **2 Global Exception Handlers**: Separate for API and MVC  
✅ **Standardized Error Responses**: Consistent format across all endpoints  
✅ **Request Correlation**: Unique request ID in all logs  
✅ **Performance Monitoring**: Automatic slow request detection  
✅ **Profile-Aware Logging**: Different log levels per environment  
✅ **Structured Logging**: Easy to parse and analyze  
✅ **Security Ready**: Prepared for security event logging

---

**Last Updated**: November 7, 2025  
**Version**: 1.0  
**Task**: 1.5 - Error Handling & Logging Infrastructure
