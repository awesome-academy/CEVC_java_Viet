package com.sunbooking.util;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.sunbooking.entity.Review;
import com.sunbooking.exception.DataIntegrityException;

import lombok.RequiredArgsConstructor;

/**
 * Utility class for validating review data integrity.
 * Ensures review entities have required relationships before DTO conversion.
 */
@Component
@RequiredArgsConstructor
public class ReviewValidator {

    private final MessageSource messageSource;

    /**
     * Validate that review and its required relationships are not null.
     *
     * @param review the review entity to validate
     * @throws DataIntegrityException if validation fails
     */
    public void validateReviewIntegrity(Review review) {
        if (review == null) {
            throw new DataIntegrityException(
                    messageSource.getMessage("error.review.null",
                            null,
                            LocaleContextHolder.getLocale()));
        }

        if (review.getUser() == null) {
            throw new DataIntegrityException(
                    messageSource.getMessage("error.review.missing.user",
                            null,
                            LocaleContextHolder.getLocale()));
        }

        if (review.getCategory() == null) {
            throw new DataIntegrityException(
                    messageSource.getMessage("error.review.missing.category",
                            null,
                            LocaleContextHolder.getLocale()));
        }
    }
}
