package com.sunbooking.service.admin;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sunbooking.dto.admin.dashboard.RecentBookingDTO;
import com.sunbooking.dto.admin.dashboard.RecentReviewDTO;
import com.sunbooking.entity.BookingStatus;
import com.sunbooking.entity.PaymentStatus;
import com.sunbooking.entity.UserRole;
import com.sunbooking.repository.BookingRepository;
import com.sunbooking.repository.CategoryRepository;
import com.sunbooking.repository.ReviewRepository;
import com.sunbooking.repository.TourRepository;
import com.sunbooking.repository.UserRepository;

/**
 * Service for managing dashboard statistics and data.
 * Provides methods to retrieve summary statistics, revenue data, and recent
 * activities.
 */
@Service
@Transactional
public class DashboardService {

    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    /**
     * Get total number of active users (role = USER).
     *
     * @return total count of active users
     */
    @Transactional(readOnly = true)
    public long getTotalUsers() {
        logger.debug("Getting total active users count");
        return userRepository.countByRoleAndIsActive(UserRole.USER, true);
    }

    /**
     * Get total number of all bookings.
     *
     * @return total count of bookings
     */
    @Transactional(readOnly = true)
    public long getTotalBookings() {
        logger.debug("Getting total bookings count");
        return bookingRepository.count();
    }

    /**
     * Get number of pending bookings.
     *
     * @return count of bookings with status = PENDING
     */
    @Transactional(readOnly = true)
    public long getPendingBookings() {
        logger.debug("Getting pending bookings count");
        return bookingRepository.countByStatus(BookingStatus.PENDING);
    }

    /**
     * Get total revenue from all paid bookings.
     *
     * @return total revenue amount
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenue() {
        logger.debug("Calculating total revenue");
        Double revenue = bookingRepository.calculateTotalRevenue();
        return revenue != null ? BigDecimal.valueOf(revenue) : BigDecimal.ZERO;
    }

    /**
     * Get monthly revenue for the last 12 months.
     *
     * @return map of month (YYYY-MM) to revenue amount
     */
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getMonthlyRevenue() {
        logger.debug("Getting monthly revenue for last 12 months");
        LocalDateTime startDate = LocalDateTime.now().minusMonths(12);
        List<Object[]> results = bookingRepository.getMonthlyRevenue(startDate);

        Map<String, BigDecimal> monthlyRevenue = new LinkedHashMap<>();
        for (Object[] result : results) {
            String month = (String) result[0];
            Double amount = (Double) result[1];
            monthlyRevenue.put(month, amount != null ? BigDecimal.valueOf(amount) : BigDecimal.ZERO);
        }

        logger.debug("Found revenue data for {} months", monthlyRevenue.size());
        return monthlyRevenue;
    }

    /**
     * Get revenue breakdown by payment status.
     *
     * @return map of payment status to revenue amount
     */
    @Transactional(readOnly = true)
    public Map<PaymentStatus, BigDecimal> getRevenueBreakdown() {
        logger.debug("Getting revenue breakdown by payment status");
        List<Object[]> results = bookingRepository.getRevenueByPaymentStatus();

        Map<PaymentStatus, BigDecimal> breakdown = new LinkedHashMap<>();
        for (Object[] result : results) {
            PaymentStatus status = (PaymentStatus) result[0];
            Double amount = (Double) result[1];
            breakdown.put(status, amount != null ? BigDecimal.valueOf(amount) : BigDecimal.ZERO);
        }

        logger.debug("Revenue breakdown: {}", breakdown);
        return breakdown;
    }

    /**
     * Get recent bookings (latest first).
     *
     * @param limit maximum number of bookings to return
     * @return list of recent bookings
     */
    @Transactional(readOnly = true)
    public List<RecentBookingDTO> getRecentBookings(int limit) {
        logger.debug("Getting {} recent bookings", limit);
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        Page<RecentBookingDTO> bookings = bookingRepository.findAll(pageable)
                .map(RecentBookingDTO::fromEntity);

        logger.debug("Found {} recent bookings", bookings.getContent().size());
        return bookings.getContent();
    }

    /**
     * Get recent reviews (latest first).
     *
     * @param limit maximum number of reviews to return
     * @return list of recent reviews
     */
    @Transactional(readOnly = true)
    public List<RecentReviewDTO> getRecentReviews(int limit) {
        logger.debug("Getting {} recent reviews", limit);
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        Page<RecentReviewDTO> reviews = reviewRepository.findAll(pageable)
                .map(RecentReviewDTO::fromEntity);

        logger.debug("Found {} recent reviews", reviews.getContent().size());
        return reviews.getContent();
    }

    /**
     * Get number of active tours.
     *
     * @return count of active tours
     */
    @Transactional(readOnly = true)
    public long getActiveTours() {
        logger.debug("Getting active tours count");
        return tourRepository.countByIsActive(true);
    }

    /**
     * Get total number of categories (not soft-deleted).
     *
     * @return count of categories
     */
    @Transactional(readOnly = true)
    public long getTotalCategories() {
        logger.debug("Getting total categories count");
        return categoryRepository.findAllActive().size();
    }

    /**
     * Get total amount of pending payments.
     *
     * @return total amount with payment status = PENDING
     */
    @Transactional(readOnly = true)
    public BigDecimal getPendingPayments() {
        logger.debug("Calculating pending payments");
        Double pendingAmount = bookingRepository.sumByPaymentStatus(PaymentStatus.PENDING);
        return pendingAmount != null ? BigDecimal.valueOf(pendingAmount) : BigDecimal.ZERO;
    }
}
