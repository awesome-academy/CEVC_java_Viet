package com.sunbooking.util;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.sunbooking.entity.Booking;
import com.sunbooking.exception.DataIntegrityException;

import lombok.RequiredArgsConstructor;

/**
 * Utility class for validating booking data integrity.
 * Ensures booking entities have required relationships before DTO conversion.
 */
@Component
@RequiredArgsConstructor
public class BookingValidator {

    private final MessageSource messageSource;

    /**
     * Validate that booking and its required relationships are not null.
     *
     * @param booking the booking entity to validate
     * @throws DataIntegrityException if validation fails
     */
    public void validateBookingIntegrity(Booking booking) {
        if (booking == null) {
            throw new DataIntegrityException(
                    messageSource.getMessage("error.booking.null",
                            null,
                            LocaleContextHolder.getLocale()));
        }

        if (booking.getUser() == null) {
            throw new DataIntegrityException(
                    messageSource.getMessage("error.booking.missing.user",
                            null,
                            LocaleContextHolder.getLocale()));
        }

        if (booking.getTour() == null) {
            throw new DataIntegrityException(
                    messageSource.getMessage("error.booking.missing.tour",
                            null,
                            LocaleContextHolder.getLocale()));
        }
    }
}
