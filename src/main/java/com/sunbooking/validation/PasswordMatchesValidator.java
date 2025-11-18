package com.sunbooking.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;

/**
 * Validator for PasswordMatches annotation.
 * Checks if password and confirmPassword fields have the same value.
 */
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    private String passwordField;
    private String confirmPasswordField;

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        this.passwordField = constraintAnnotation.passwordField();
        this.confirmPasswordField = constraintAnnotation.confirmPasswordField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        BeanWrapperImpl wrapper = new BeanWrapperImpl(value);
        Object password = wrapper.getPropertyValue(passwordField);
        Object confirmPassword = wrapper.getPropertyValue(confirmPasswordField);

        // If both are null or empty, it's valid (for edit without password change)
        if ((password == null || password.toString().trim().isEmpty())
                && (confirmPassword == null || confirmPassword.toString().trim().isEmpty())) {
            return true;
        }

        // If one is null/empty and the other is not, they don't match
        if (password == null || confirmPassword == null) {
            return false;
        }

        // Check if they match
        boolean isValid = password.equals(confirmPassword);

        // Customize error message location
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(confirmPasswordField)
                    .addConstraintViolation();
        }

        return isValid;
    }
}
