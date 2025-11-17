package com.sunbooking.dto.admin.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.sunbooking.entity.User;
import com.sunbooking.entity.UserRole;
import com.sunbooking.validation.PasswordMatches;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Form DTO for creating and editing users in admin panel.
 * Includes validation rules for all user fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches
public class UserForm {

    private Long id;

    @NotBlank(message = "{validation.name.required}")
    @Size(max = 255, message = "{validation.name.size}")
    private String name;

    @NotBlank(message = "{validation.email.required}")
    @Email(message = "{validation.email.invalid}")
    @Size(max = 255, message = "{validation.email.size}")
    private String email;

    @Size(min = 6, max = 255, message = "{validation.password.size}")
    private String password;

    private String confirmPassword;

    @Pattern(regexp = "^$|^[0-9+\\-\\s()]+$", message = "{validation.phone.pattern}")
    @Size(max = 50, message = "{validation.phone.size}")
    private String phone;

    // Role is set programmatically in controller (always USER for user management)
    private UserRole role;

    @NotNull(message = "{validation.isActive.required}")
    private Boolean isActive;

    /**
     * Create UserForm from User entity (for edit).
     *
     * @param user the user entity
     * @return the user form
     */
    public static UserForm fromEntity(User user) {
        UserForm form = new UserForm();
        form.setId(user.getId());
        form.setName(user.getName());
        form.setEmail(user.getEmail());
        form.setPhone(user.getPhone());
        form.setRole(user.getRole());
        form.setIsActive(user.getIsActive());
        // Don't populate password for security
        return form;
    }

    /**
     * Create UserForm from UserDTO (for edit in controller).
     *
     * @param dto the user DTO
     * @return the user form
     */
    public static UserForm fromDTO(com.sunbooking.dto.admin.user.UserDTO dto) {
        UserForm form = new UserForm();
        form.setId(dto.getId());
        form.setName(dto.getName());
        form.setEmail(dto.getEmail());
        form.setPhone(dto.getPhone());
        form.setRole(dto.getRole());
        form.setIsActive(dto.getIsActive());
        // Don't populate password for security
        return form;
    }

    /**
     * Convert form to User entity (for create).
     *
     * @return new user entity
     */
    public User toEntity() {
        User user = new User();
        user.setName(this.name);
        user.setEmail(this.email);
        user.setPassword(this.password); // Will be encoded by service
        user.setPhone(this.phone);
        user.setRole(this.role);
        user.setIsActive(this.isActive);
        return user;
    }

    /**
     * Update existing user entity with form data.
     *
     * @param user the user entity to update
     */
    public void updateEntity(User user) {
        user.setName(this.name);
        user.setEmail(this.email);
        user.setPhone(this.phone);
        user.setRole(this.role);
        user.setIsActive(this.isActive);
        // Password is updated separately if provided
    }

    /**
     * Check if this is a new user (create) or existing (edit).
     *
     * @return true if editing existing user, false if creating new
     */
    public boolean isEdit() {
        return id != null;
    }

    /**
     * Check if password needs validation (required for create, optional for edit).
     *
     * @return true if password should be validated
     */
    public boolean isPasswordRequired() {
        return !isEdit();
    }
}
