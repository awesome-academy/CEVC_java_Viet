package com.sunbooking.dto.api.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user registration request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User registration request payload")
public class RegisterRequest {

    @NotBlank(message = "{validation.name.required}")
    @Size(max = 255, message = "{validation.name.size}")
    @Schema(description = "User's full name", example = "John Doe", required = true)
    private String name;

    @NotBlank(message = "{validation.email.required}")
    @Email(message = "{validation.email.invalid}")
    @Size(max = 255, message = "{validation.email.size}")
    @Schema(description = "User's email address (must be unique)", example = "john.doe@example.com", required = true)
    private String email;

    @NotBlank(message = "{validation.password.required}")
    @Size(min = 6, max = 255, message = "{validation.password.size}")
    @Schema(description = "User's password (minimum 6 characters)", example = "SecurePass123", required = true, minLength = 6)
    private String password;

    @Size(max = 50, message = "{validation.phone.size}")
    @Schema(description = "User's phone number (optional)", example = "0123456789")
    private String phone;
}
