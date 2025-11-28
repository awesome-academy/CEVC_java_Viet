# Swagger Documentation Structure

This document explains the modular Swagger documentation architecture used in this project.

## Overview

Instead of cluttering controllers with inline Swagger annotations, we've implemented a clean, modular documentation structure that separates API documentation from business logic.

## Architecture

```
src/main/java/com/sunbooking/
├── controller/
│   ├── base/
│   │   └── BaseController.java          # Base controller with common responses
│   └── api/
│       └── AuthController.java          # Clean controller with minimal annotations
├── swagger/
│   ├── CommonResponses.java             # Reusable response annotations
│   ├── AuthSwaggerDoc.java              # Authentication endpoint documentation
│   ├── examples/
│   │   └── SwaggerExamples.java         # Centralized JSON examples
│   └── schemas/
│       └── ApiSchemas.java              # Reusable schema definitions
└── config/
    ├── OpenApiConfig.java               # Main Swagger configuration
    └── SwaggerSecurityConfig.java       # Security configuration for Swagger
```

## Components

### 1. BaseController

**Purpose**: Provides common functionality and documentation for all controllers.

- Common error responses (500 Internal Server Error)
- Consistent response structure
- Future: Common exception handling

### 2. CommonResponses

**Purpose**: Reusable response annotations for standard HTTP responses.

- `@BadRequest` - 400 Bad Request with validation errors
- `@Unauthorized` - 401 Unauthorized
- `@RateLimited` - 429 Too Many Requests
- `@InternalServerError` - 500 Internal Server Error

### 3. AuthSwaggerDoc

**Purpose**: Centralized documentation for authentication endpoints.

- Custom annotations: `@RegisterEndpoint`, `@LoginEndpoint`
- Complete API documentation with examples
- Error scenarios and response codes
- Request/response schemas

### 4. SwaggerExamples

**Purpose**: Centralized JSON examples for consistency.

- Registration request/response examples
- Login request/response examples
- Error response examples (validation, rate limit, etc.)
- Reusable across multiple endpoints

### 5. ApiSchemas

**Purpose**: Reusable Swagger schema definitions.

- `ApiResponseSchema` - Standard API response structure
- `ValidationErrorSchema` - Validation error details
- `RateLimitErrorSchema` - Rate limiting error details
- Consistent schema definitions across API

## Benefits

### 1. **Clean Controllers**

Controllers focus solely on business logic without documentation clutter:

```java
@AuthSwaggerDoc.RegisterEndpoint
@PostMapping("/register")
public ResponseEntity<AuthResponse> register(
    @Valid @RequestBody RegisterRequest request,
    HttpServletRequest httpRequest
) {
    // Business logic only
}
```

### 2. **Centralized Documentation**

All API documentation in dedicated classes:

- Easier to maintain and update
- Consistent documentation style
- Reusable components
- Better organization

### 3. **Reusable Components**

Common responses, examples, and schemas can be reused:

```java
@AuthSwaggerDoc.RegisterEndpoint  // Uses centralized documentation
@CommonResponses.BadRequest       // Uses common response
@CommonResponses.RateLimited      // Uses common response
```

### 4. **Type Safety**

Custom annotations provide compile-time checking:

- No typos in response codes
- Consistent parameter documentation
- IDE auto-completion support

## Usage

### Adding New Endpoints

1. **Create endpoint documentation class**:

```java
public class UserSwaggerDoc {
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Get user profile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
        @CommonResponses.Unauthorized.class,
        @CommonResponses.InternalServerError.class
    })
    public @interface GetProfile {}
}
```

2. **Add examples and schemas as needed**:

```java
// In SwaggerExamples.java
public static final String USER_PROFILE_EXAMPLE = """
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@example.com",
    "fullName": "John Doe"
  }
}
""";
```

3. **Use in controller**:

```java
@UserSwaggerDoc.GetProfile
@GetMapping("/profile")
public ResponseEntity<UserResponse> getProfile() {
    // Business logic
}
```

### Adding Common Responses

Add new common response annotations to `CommonResponses.java`:

```java
@ApiResponse(
    responseCode = "403",
    description = "Access forbidden",
    content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ApiSchemas.ApiResponseSchema.class)
    )
)
public @interface Forbidden {}
```

## Swagger UI Access

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **OpenAPI YAML**: http://localhost:8080/v3/api-docs.yaml

## Security

Swagger endpoints are secured and require authentication in production:

- Development: Open access for testing
- Production: Requires admin authentication
- Configurable via `SwaggerSecurityConfig.java`

## Future Enhancements

1. **API Versioning**: Support for multiple API versions
2. **Response Interceptors**: Automatic response wrapping
3. **Request/Response Logging**: Centralized API logging
4. **Rate Limiting Documentation**: Enhanced rate limiting docs
5. **Error Code Registry**: Centralized error code management

This modular approach ensures maintainable, consistent, and professional API documentation while keeping controllers clean and focused on business logic.
