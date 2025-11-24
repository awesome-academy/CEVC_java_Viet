package com.sunbooking.specification;

import java.time.LocalDateTime;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import com.sunbooking.dto.admin.booking.BookingSearchCriteria;
import com.sunbooking.entity.Booking;
import com.sunbooking.entity.BookingStatus;
import com.sunbooking.entity.PaymentStatus;
import com.sunbooking.entity.Tour;
import com.sunbooking.entity.User;

/**
 * JPA Specification for Booking entity.
 * Provides dynamic query building for booking search and filtering.
 */
public class BookingSpecification {

    private static final Logger logger = LoggerFactory.getLogger(BookingSpecification.class);

    private BookingSpecification() {
        // Private constructor to prevent instantiation
    }

    /**
     * Build JPA Specification from search criteria.
     *
     * @param criteria the search criteria
     * @return JPA Specification for Booking
     */
    public static Specification<Booking> buildSpecification(BookingSearchCriteria criteria) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            // Join with User and Tour for searching
            Join<Booking, User> userJoin = root.join("user", JoinType.LEFT);
            Join<Booking, Tour> tourJoin = root.join("tour", JoinType.LEFT);

            // Search by keyword (booking code, user name, tour title)
            if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
                String keyword = "%" + criteria.getKeyword().toLowerCase() + "%";
                Predicate keywordPredicate = cb.or(
                        cb.like(cb.lower(root.get("bookingCode")), keyword),
                        cb.like(cb.lower(userJoin.get("name")), keyword),
                        cb.like(cb.lower(tourJoin.get("title")), keyword));
                predicate = cb.and(predicate, keywordPredicate);
            }

            // Filter by booking status
            if (criteria.getStatus() != null && !criteria.getStatus().trim().isEmpty()
                    && !"all".equalsIgnoreCase(criteria.getStatus())) {
                try {
                    BookingStatus status = BookingStatus.valueOf(criteria.getStatus().toUpperCase());
                    predicate = cb.and(predicate, cb.equal(root.get("status"), status));
                } catch (IllegalArgumentException e) {
                    logger.warn("Invalid booking status: {}", criteria.getStatus());
                }
            }

            // Filter by payment status
            if (criteria.getPaymentStatus() != null && !criteria.getPaymentStatus().trim().isEmpty()
                    && !"all".equalsIgnoreCase(criteria.getPaymentStatus())) {
                try {
                    PaymentStatus paymentStatus = PaymentStatus.valueOf(criteria.getPaymentStatus().toUpperCase());
                    predicate = cb.and(predicate, cb.equal(root.get("paymentStatus"), paymentStatus));
                } catch (IllegalArgumentException e) {
                    logger.warn("Invalid payment status: {}", criteria.getPaymentStatus());
                }
            }

            // Filter by date range - from date
            if (criteria.getFromDate() != null) {
                LocalDateTime fromDateTime = criteria.getFromDate().atStartOfDay();
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("bookingDate"), fromDateTime));
            }

            // Filter by date range - to date
            if (criteria.getToDate() != null) {
                LocalDateTime toDateTime = criteria.getToDate().atTime(23, 59, 59);
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("bookingDate"), toDateTime));
            }

            return predicate;
        };
    }

    /**
     * Specification to find bookings by user ID.
     *
     * @param userId the user ID
     * @return JPA Specification
     */
    public static Specification<Booking> hasUserId(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) {
                return cb.conjunction();
            }
            Join<Booking, User> userJoin = root.join("user", JoinType.INNER);
            return cb.equal(userJoin.get("id"), userId);
        };
    }

    /**
     * Specification to find bookings by tour ID.
     *
     * @param tourId the tour ID
     * @return JPA Specification
     */
    public static Specification<Booking> hasTourId(Long tourId) {
        return (root, query, cb) -> {
            if (tourId == null) {
                return cb.conjunction();
            }
            Join<Booking, Tour> tourJoin = root.join("tour", JoinType.INNER);
            return cb.equal(tourJoin.get("id"), tourId);
        };
    }

    /**
     * Specification to find bookings by status.
     *
     * @param status the booking status
     * @return JPA Specification
     */
    public static Specification<Booking> hasStatus(BookingStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("status"), status);
        };
    }

    /**
     * Specification to find bookings by payment status.
     *
     * @param paymentStatus the payment status
     * @return JPA Specification
     */
    public static Specification<Booking> hasPaymentStatus(PaymentStatus paymentStatus) {
        return (root, query, cb) -> {
            if (paymentStatus == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("paymentStatus"), paymentStatus);
        };
    }

    /**
     * Specification to find bookings by booking code.
     *
     * @param bookingCode the booking code
     * @return JPA Specification
     */
    public static Specification<Booking> hasBookingCode(String bookingCode) {
        return (root, query, cb) -> {
            if (bookingCode == null || bookingCode.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("bookingCode"), bookingCode);
        };
    }

    /**
     * Specification to find bookings within a date range.
     *
     * @param fromDate the start date
     * @param toDate   the end date
     * @return JPA Specification
     */
    public static Specification<Booking> betweenDates(LocalDateTime fromDate, LocalDateTime toDate) {
        return (root, query, cb) -> {
            if (fromDate == null && toDate == null) {
                return cb.conjunction();
            }
            if (fromDate != null && toDate != null) {
                return cb.between(root.get("bookingDate"), fromDate, toDate);
            }
            if (fromDate != null) {
                return cb.greaterThanOrEqualTo(root.get("bookingDate"), fromDate);
            }
            return cb.lessThanOrEqualTo(root.get("bookingDate"), toDate);
        };
    }
}
