package com.sunbooking.dto.admin.admin;

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
 * Form DTO for creating and editing admin accounts.
 * Includes validation rules.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches
public class AdminForm {

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

    @Size(max = 20, message = "{validation.phone.size}")
    @Pattern(regexp = "^$|^[0-9+\\-\\s()]+$", message = "{validation.phone.pattern}")
    private String phone;

    @NotNull(message = "{validation.isActive.required}")
    private Boolean isActive;

    /**
     * Create form from existing admin entity.
     */
    public static AdminForm fromEntity(User user) {
        AdminForm form = new AdminForm();
        form.setId(user.getId());
        form.setName(user.getName());
        form.setEmail(user.getEmail());
        form.setPhone(user.getPhone());
        form.setIsActive(user.getIsActive());
        // Note: Password is never populated from entity for security
        return form;
    }

    /**
     * Convert form to new User entity.
     * Used for creating new admins.
     */
    public User toEntity() {
        User user = new User();
        user.setName(this.name);
        user.setEmail(this.email);
        user.setPhone(this.phone);
        user.setRole(UserRole.ADMIN); // Always ADMIN role
        user.setIsActive(this.isActive);
        return user;
    }

    /**
     * Update existing User entity with form data.
     * Used for editing admins.
     */
    public void updateEntity(User user) {
        user.setName(this.name);
        user.setEmail(this.email);
        user.setPhone(this.phone);
        user.setIsActive(this.isActive);
        // Password is updated separately if provided
    }

    /**
     * Check if this is an edit operation (id exists).
     */
    public boolean isEdit() {
        return id != null;
    }

    /**
     * Check if password is required.
     * Required for new admins, optional for editing.
     */
    public boolean isPasswordRequired() {
        return !isEdit();
    }
}
