package com.sunbooking.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Sun Booking Tour API", version = "1.0.0", description = "API documentation for Sun Booking Tour application - Travel booking system with tours, reviews, and user management", contact = @Contact(name = "Sun Booking Team", email = "support@sunbooking.com", url = "https://sunbooking.com"), license = @License(name = "MIT License", url = "https://opensource.org/licenses/MIT")), servers = {
        @Server(url = "http://localhost:8080", description = "Development Server"),
        @Server(url = "https://api.sunbooking.com", description = "Production Server")
}, security = @SecurityRequirement(name = "Bearer Authentication"))
@SecurityScheme(name = "Bearer Authentication", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer", description = "JWT token for API authentication. Format: 'Bearer {token}'")
public class OpenApiConfig {
}