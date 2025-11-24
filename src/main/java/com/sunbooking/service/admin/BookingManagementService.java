package com.sunbooking.service.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sunbooking.dto.admin.booking.BookingDTO;
import com.sunbooking.dto.admin.booking.BookingDetailDTO;
import com.sunbooking.dto.admin.booking.BookingListDTO;
import com.sunbooking.dto.admin.booking.BookingSearchCriteria;
import com.sunbooking.dto.admin.booking.BookingStatusForm;
import com.sunbooking.entity.Booking;
import com.sunbooking.entity.BookingStatus;
import com.sunbooking.entity.PaymentStatus;
import com.sunbooking.exception.ResourceNotFoundException;
import com.sunbooking.exception.ValidationException;
import com.sunbooking.repository.BookingRepository;
import com.sunbooking.specification.BookingSpecification;
import com.sunbooking.util.BookingValidator;

/**
 * Service for managing bookings in admin panel.
 * Handles booking listing, filtering, viewing, and status updates.
 */
@Service
public class BookingManagementService {

    private static final Logger logger = LoggerFactory.getLogger(BookingManagementService.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private BookingValidator bookingValidator;

    /**
     * Get all bookings with search and filter criteria.
     *
     * @param criteria the search criteria
     * @return page of booking list DTOs
     */
    @Transactional(readOnly = true)
    public Page<BookingListDTO> getAllBookings(BookingSearchCriteria criteria) {
        logger.debug("Getting all bookings with criteria: {}", criteria);

        Sort sort = Sort.by(
                "DESC".equalsIgnoreCase(criteria.getSortDir()) ? Sort.Direction.DESC : Sort.Direction.ASC,
                criteria.getSortBy());

        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize(), sort);
        Specification<Booking> spec = BookingSpecification.buildSpecification(criteria);

        return bookingRepository.findAll(spec, pageable)
                .map(booking -> {
                    bookingValidator.validateBookingIntegrity(booking);
                    return BookingListDTO.fromEntity(booking);
                });
    }

    /**
     * Get booking by ID.
     *
     * @param id the booking ID
     * @return booking DTO if found
     */
    @Transactional(readOnly = true)
    public BookingDTO getBookingById(Long id) {
        logger.debug("Getting booking by id: {}", id);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("error.booking.not.found",
                                new Object[] { id },
                                LocaleContextHolder.getLocale())));

        bookingValidator.validateBookingIntegrity(booking);
        return BookingDTO.fromEntity(booking);
    }

    /**
     * Get detailed booking information by ID.
     *
     * @param id the booking ID
     * @return detailed booking DTO if found
     */
    @Transactional(readOnly = true)
    public BookingDetailDTO getBookingDetailById(Long id) {
        logger.debug("Getting booking detail by id: {}", id);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("error.booking.not.found",
                                new Object[] { id },
                                LocaleContextHolder.getLocale())));

        bookingValidator.validateBookingIntegrity(booking);
        return BookingDetailDTO.fromEntity(booking);
    }

    /**
     * Update booking status and payment status.
     *
     * @param id   the booking ID
     * @param form the status update form
     */
    @Transactional
    public void updateBookingStatus(Long id, BookingStatusForm form) {
        logger.info("Updating booking status for id: {}", id);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("error.booking.not.found",
                                new Object[] { id },
                                LocaleContextHolder.getLocale())));

        // Validate status transition
        validateStatusTransition(booking, form);

        // Validate cancel reason if status is CANCELLED
        if (form.getStatus() == BookingStatus.CANCELLED) {
            if (form.getCancelReason() == null || form.getCancelReason().trim().isEmpty()) {
                throw new ValidationException(
                        messageSource.getMessage("error.booking.cancel.reason.required",
                                null,
                                LocaleContextHolder.getLocale()));
            }
            booking.setCancelReason(form.getCancelReason());
        }

        BookingStatus oldStatus = booking.getStatus();
        PaymentStatus oldPaymentStatus = booking.getPaymentStatus();

        booking.setStatus(form.getStatus());
        booking.setPaymentStatus(form.getPaymentStatus());

        bookingRepository.save(booking);

        logger.info("Booking {} status updated: {} -> {}, payment: {} -> {}",
                booking.getBookingCode(), oldStatus, form.getStatus(),
                oldPaymentStatus, form.getPaymentStatus());
    }

    /**
     * Validate status transition rules.
     *
     * @param booking the current booking
     * @param form    the new status form
     * @throws ValidationException if transition is invalid
     */
    private void validateStatusTransition(Booking booking, BookingStatusForm form) {
        // Cannot change status from CANCELLED to other status
        if (booking.getStatus() == BookingStatus.CANCELLED
                && form.getStatus() != BookingStatus.CANCELLED) {
            throw new ValidationException(
                    messageSource.getMessage("error.booking.status.transition",
                            new Object[] { booking.getStatus(), form.getStatus() },
                            LocaleContextHolder.getLocale()));
        }

        // Warn if CONFIRMED but payment not PAID
        if (form.getStatus() == BookingStatus.CONFIRMED
                && form.getPaymentStatus() != PaymentStatus.PAID) {
            logger.warn("Booking {} confirmed but payment status is {}",
                    booking.getBookingCode(), form.getPaymentStatus());
        }
    }
}
