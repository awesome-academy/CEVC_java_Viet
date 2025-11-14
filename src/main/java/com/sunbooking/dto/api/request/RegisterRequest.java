package com.sunbooking.dto.api.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user registration request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "{validation.name.required}")
    @Size(max = 255, message = "{validation.name.size}")
    private String name;

    @NotBlank(message = "{validation.email.required}")
    @Email(message = "{validation.email.invalid}")
    @Size(max = 255, message = "{validation.email.size}")
    private String email;

    @NotBlank(message = "{validation.password.required}")
    @Size(min = 6, max = 255, message = "{validation.password.size}")
    private String password;

    @Size(max = 50, message = "{validation.phone.size}")
    private String phone;
}
