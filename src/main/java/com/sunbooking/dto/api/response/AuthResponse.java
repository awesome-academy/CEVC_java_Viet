package com.sunbooking.dto.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication response with JWT token.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private UserDTO user;

    // Constructor for backward compatibility
    public AuthResponse(String token, String type, Long id, String email, String name, String role) {
        this.token = token;
        this.user = new UserDTO();
        this.user.setId(id);
        this.user.setEmail(email);
        this.user.setName(name);
    }
}
