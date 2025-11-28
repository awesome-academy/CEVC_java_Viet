package com.sunbooking.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunbooking.entity.Booking;
import com.sunbooking.entity.BookingStatus;
import com.sunbooking.entity.PaymentStatus;

/**
 * Repository interface for Booking entity.
 * Provides CRUD operations and custom query methods for booking management.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

        /**
         * Find a booking by booking code.
         *
         * @param bookingCode the unique booking code
         * @return an Optional containing the booking if found, or empty if not found
         */
        Optional<Booking> findByBookingCode(String bookingCode);

        /**
         * Check if a booking code already exists.
         *
         * @param bookingCode the booking code to check
         * @return true if the booking code exists, false otherwise
         */
        boolean existsByBookingCode(String bookingCode);

        /**
         * Find all bookings for a specific user.
         *
         * @param userId   the user ID
         * @param pageable pagination information
         * @return a page of bookings for the user
         */
        Page<Booking> findByUserId(Long userId, Pageable pageable);

        /**
         * Find all bookings for a specific tour.
         *
         * @param tourId   the tour ID
         * @param pageable pagination information
         * @return a page of bookings for the tour
         */
        Page<Booking> findByTourId(Long tourId, Pageable pageable);

        /**
         * Find bookings by status.
         *
         * @param status   the booking status
         * @param pageable pagination information
         * @return a page of bookings with the specified status
         */
        Page<Booking> findByStatus(BookingStatus status, Pageable pageable);

        /**
         * Find bookings by payment status.
         *
         * @param paymentStatus the payment status
         * @param pageable      pagination information
         * @return a page of bookings with the specified payment status
         */
        Page<Booking> findByPaymentStatus(PaymentStatus paymentStatus, Pageable pageable);

        /**
         * Find bookings by user and status.
         *
         * @param userId   the user ID
         * @param status   the booking status
         * @param pageable pagination information
         * @return a page of bookings matching the criteria
         */
        Page<Booking> findByUserIdAndStatus(Long userId, BookingStatus status, Pageable pageable);

        /**
         * Find bookings by tour and status.
         *
         * @param tourId   the tour ID
         * @param status   the booking status
         * @param pageable pagination information
         * @return a page of bookings matching the criteria
         */
        Page<Booking> findByTourIdAndStatus(Long tourId, BookingStatus status, Pageable pageable);

        /**
         * Find bookings created within a date range.
         *
         * @param startDate the start date
         * @param endDate   the end date
         * @param pageable  pagination information
         * @return a page of bookings created within the date range
         */
        Page<Booking> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

        /**
         * Find bookings by status and payment status.
         *
         * @param status        the booking status
         * @param paymentStatus the payment status
         * @param pageable      pagination information
         * @return a page of bookings matching both statuses
         */
        Page<Booking> findByStatusAndPaymentStatus(BookingStatus status,
                        PaymentStatus paymentStatus,
                        Pageable pageable);

        /**
         * Get recent bookings (latest first).
         *
         * @param pageable pagination information
         * @return a page of recent bookings
         */
        @Query("SELECT b FROM Booking b ORDER BY b.createdAt DESC")
        Page<Booking> findRecentBookings(Pageable pageable);

        /**
         * Count bookings by status.
         *
         * @param status the booking status
         * @return the number of bookings with the specified status
         */
        long countByStatus(BookingStatus status);

        /**
         * Count bookings by payment status.
         *
         * @param paymentStatus the payment status
         * @return the number of bookings with the specified payment status
         */
        long countByPaymentStatus(PaymentStatus paymentStatus);

        /**
         * Count bookings for a specific user.
         *
         * @param userId the user ID
         * @return the number of bookings for the user
         */
        long countByUserId(Long userId);

        /**
         * Count bookings for a specific tour.
         *
         * @param tourId the tour ID
         * @return the number of bookings for the tour
         */
        long countByTourId(Long tourId);

        /**
         * Calculate total revenue (sum of all paid bookings).
         *
         * @return the total revenue
         */
        @Query("SELECT COALESCE(SUM(t.price), 0) FROM Booking b JOIN b.tour t " +
                        "WHERE b.paymentStatus = 'PAID'")
        Double calculateTotalRevenue();

        /**
         * Calculate total revenue within a date range.
         *
         * @param startDate the start date
         * @param endDate   the end date
         * @return the total revenue within the date range
         */
        @Query("SELECT COALESCE(SUM(t.price), 0) FROM Booking b JOIN b.tour t " +
                        "WHERE b.paymentStatus = 'PAID' AND b.createdAt BETWEEN :startDate AND :endDate")
        Double calculateRevenueByDateRange(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        /**
         * Check if a user has a confirmed and paid booking for a specific tour.
         *
         * @param userId the user ID
         * @param tourId the tour ID
         * @return true if the user has completed a booking for the tour, false
         *         otherwise
         */
        @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b " +
                        "WHERE b.user.id = :userId AND b.tour.id = :tourId " +
                        "AND b.status = 'CONFIRMED' AND b.paymentStatus = 'PAID'")
        boolean hasCompletedBooking(@Param("userId") Long userId, @Param("tourId") Long tourId);

        /**
         * Check if a user has already rated a specific booking.
         *
         * @param userId the user ID
         * @param tourId the tour ID
         * @return true if the user has already rated the booking, false otherwise
         */
        @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b " +
                        "WHERE b.user.id = :userId AND b.tour.id = :tourId AND b.rating IS NOT NULL")
        boolean hasRated(@Param("userId") Long userId, @Param("tourId") Long tourId);

        /**
         * Find bookings with ratings for a specific tour.
         *
         * @param tourId the tour ID
         * @return a list of bookings with ratings
         */
        @Query("SELECT b FROM Booking b WHERE b.tour.id = :tourId AND b.rating IS NOT NULL")
        List<Booking> findRatedBookingsByTourId(@Param("tourId") Long tourId);

        /**
         * Count bookings by tour and status list.
         *
         * @param tourId   the tour ID
         * @param statuses the list of booking statuses
         * @return the number of bookings matching the criteria
         */
        @Query("SELECT COUNT(b) FROM Booking b WHERE b.tour.id = :tourId AND b.status IN :statuses")
        long countByTourIdAndStatusIn(@Param("tourId") Long tourId, @Param("statuses") List<BookingStatus> statuses);

        /**
         * Calculate total revenue for a specific tour by payment status.
         *
         * @param tourId        the tour ID
         * @param paymentStatus the payment status
         * @return the total revenue for the tour
         */
        @Query("SELECT COALESCE(SUM(t.price), 0) FROM Booking b JOIN b.tour t " +
                        "WHERE t.id = :tourId AND b.paymentStatus = :paymentStatus")
        Double sumRevenueByTourAndPaymentStatus(@Param("tourId") Long tourId,
                        @Param("paymentStatus") PaymentStatus paymentStatus);

        /**
         * Calculate average rating for a specific tour.
         *
         * @param tourId the tour ID
         * @return the average rating or null if no ratings exist
         */
        @Query("SELECT AVG(b.rating) FROM Booking b WHERE b.tour.id = :tourId AND b.rating IS NOT NULL")
        Double averageRatingByTourId(@Param("tourId") Long tourId);

        /**
         * Get monthly revenue for the last 12 months.
         * Returns month in format YYYY-MM and total revenue for that month.
         *
         * @param startDate the start date (12 months ago)
         * @return list of arrays [month, revenue]
         */
        @Query("SELECT DATE_FORMAT(b.createdAt, '%Y-%m') as month, COALESCE(SUM(t.price), 0) as amount " +
                        "FROM Booking b JOIN b.tour t " +
                        "WHERE b.paymentStatus = 'PAID' AND b.createdAt >= :startDate " +
                        "GROUP BY DATE_FORMAT(b.createdAt, '%Y-%m') " +
                        "ORDER BY month")
        List<Object[]> getMonthlyRevenue(@Param("startDate") LocalDateTime startDate);

        /**
         * Get revenue breakdown by payment status.
         * Returns payment status and total revenue for each status.
         *
         * @return list of arrays [paymentStatus, revenue]
         */
        @Query("SELECT b.paymentStatus, COALESCE(SUM(t.price), 0) " +
                        "FROM Booking b JOIN b.tour t " +
                        "GROUP BY b.paymentStatus")
        List<Object[]> getRevenueByPaymentStatus();

        /**
         * Calculate sum of tour prices for bookings with specific payment status.
         *
         * @param paymentStatus the payment status
         * @return total amount or 0 if no bookings found
         */
        @Query("SELECT COALESCE(SUM(t.price), 0) FROM Booking b JOIN b.tour t " +
                        "WHERE b.paymentStatus = :paymentStatus")
        Double sumByPaymentStatus(@Param("paymentStatus") PaymentStatus paymentStatus);
}
