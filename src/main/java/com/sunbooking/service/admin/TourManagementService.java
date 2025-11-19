package com.sunbooking.service.admin;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import javax.persistence.criteria.Predicate;

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

import com.sunbooking.dto.admin.tour.TourDTO;
import com.sunbooking.dto.admin.tour.TourForm;
import com.sunbooking.dto.admin.tour.TourListDTO;
import com.sunbooking.dto.admin.tour.TourSearchCriteria;
import com.sunbooking.dto.admin.tour.TourStatisticsDTO;
import com.sunbooking.entity.BookingStatus;
import com.sunbooking.entity.PaymentStatus;
import com.sunbooking.entity.Tour;
import com.sunbooking.exception.ResourceNotFoundException;
import com.sunbooking.repository.BookingRepository;
import com.sunbooking.repository.ReviewRepository;
import com.sunbooking.repository.TourRepository;

/**
 * Implementation of TourManagementService.
 * Handles all business logic for tour management in admin panel.
 */
@Service
public class TourManagementService {

    private static final Logger logger = LoggerFactory.getLogger(TourManagementService.class);

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MessageSource messageSource;

    @Transactional(readOnly = true)
    public Page<TourListDTO> getAllTours(TourSearchCriteria criteria) {
        logger.debug("Fetching tours with criteria: {}", criteria);

        // Build specification for dynamic filtering
        Specification<Tour> spec = buildSpecification(criteria);

        // Build pageable with sorting
        Pageable pageable = PageRequest.of(
                criteria.getPage(),
                criteria.getSize(),
                buildSort(criteria.getSortBy(), criteria.getSortDir()));

        // Fetch and convert to DTO
        Page<Tour> tourPage = tourRepository.findAll(spec, pageable);
        return tourPage.map(TourListDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Optional<TourDTO> getTourById(Long id) {
        logger.debug("Fetching tour with id: {}", id);
        return tourRepository.findById(id).map(TourDTO::fromEntity);
    }

    @Transactional
    public TourDTO createTour(TourForm form) {
        logger.info("Creating new tour with title: {}", form.getTitle());

        Tour tour = form.toEntity();
        Tour savedTour = tourRepository.save(tour);

        logger.info("Tour created successfully with id: {}", savedTour.getId());
        return TourDTO.fromEntity(savedTour);
    }

    @Transactional
    public TourDTO updateTour(Long id, TourForm form) {
        logger.info("Updating tour with id: {}", id);

        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("error.entity.not.found",
                            new Object[] { "Tour", id }, LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });

        form.updateEntity(tour);
        Tour updatedTour = tourRepository.save(tour);

        logger.info("Tour updated successfully with id: {}", updatedTour.getId());
        return TourDTO.fromEntity(updatedTour);
    }

    @Transactional
    public void deleteTour(Long id) {
        logger.info("Attempting to delete tour with id: {}", id);

        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("error.entity.not.found",
                            new Object[] { "Tour", id }, LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });

        // Check for active bookings (PENDING or CONFIRMED)
        long activeBookingCount = bookingRepository.countByTourIdAndStatusIn(
                id,
                Arrays.asList(BookingStatus.PENDING, BookingStatus.CONFIRMED));

        if (activeBookingCount > 0) {
            String message = messageSource.getMessage("error.tour.has.active.bookings",
                    new Object[] { activeBookingCount }, LocaleContextHolder.getLocale());
            logger.warn("Cannot delete tour {} - has {} active bookings", id, activeBookingCount);
            throw new IllegalStateException(message);
        }

        // Soft delete
        tour.setIsActive(false);
        tour.setDeletedAt(LocalDateTime.now());
        tourRepository.save(tour);

        logger.info("Tour soft-deleted successfully with id: {}", id);
    }

    @Transactional
    public void activateTour(Long id) {
        logger.info("Activating tour with id: {}", id);

        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("error.entity.not.found",
                            new Object[] { "Tour", id }, LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });

        tour.setIsActive(true);
        tour.setDeletedAt(null);
        tourRepository.save(tour);

        logger.info("Tour activated successfully with id: {}", id);
    }

    @Transactional(readOnly = true)
    public TourStatisticsDTO getTourStatistics(Long id) {
        logger.debug("Fetching statistics for tour id: {}", id);

        // Verify tour exists
        if (!tourRepository.existsById(id)) {
            String message = messageSource.getMessage("error.entity.not.found",
                    new Object[] { "Tour", id }, LocaleContextHolder.getLocale());
            throw new ResourceNotFoundException(message);
        }

        // Get booking count
        Long bookingCount = bookingRepository.countByTourId(id);

        // Get total revenue (only PAID bookings)
        Double revenue = bookingRepository.sumRevenueByTourAndPaymentStatus(id, PaymentStatus.PAID);
        BigDecimal totalRevenue = revenue != null ? BigDecimal.valueOf(revenue) : BigDecimal.ZERO;

        // Get average rating
        Double averageRating = bookingRepository.averageRatingByTourId(id);
        if (averageRating == null) {
            averageRating = 0.0;
        }

        // Get review count
        Long reviewCount = reviewRepository.countByTourId(id);

        return new TourStatisticsDTO(bookingCount, totalRevenue, averageRating, reviewCount);
    }

    @Transactional(readOnly = true)
    public TourStatisticsDTO getOverallStatistics() {
        logger.debug("Fetching overall tour statistics");

        // Total active tours
        long totalTours = tourRepository.countByIsActive(true);

        // Total bookings
        long totalBookings = bookingRepository.count();

        // Total revenue
        Double revenue = bookingRepository.calculateTotalRevenue();
        BigDecimal totalRevenue = revenue != null ? BigDecimal.valueOf(revenue) : BigDecimal.ZERO;

        // No overall rating for all tours combined
        return new TourStatisticsDTO(totalBookings, totalRevenue, 0.0, totalTours);
    }

    @Transactional(readOnly = true)
    public boolean hasActiveBookings(Long id) {
        long count = bookingRepository.countByTourIdAndStatusIn(
                id,
                Arrays.asList(BookingStatus.PENDING, BookingStatus.CONFIRMED));
        return count > 0;
    }

    /**
     * Build JPA Specification for dynamic filtering.
     *
     * @param criteria search criteria
     * @return specification
     */
    private Specification<Tour> buildSpecification(TourSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // Filter by keyword (search in title and description)
            if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
                String keyword = "%" + criteria.getKeyword().toLowerCase() + "%";
                Predicate titlePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")), keyword);
                Predicate descPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")), keyword);
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.or(titlePredicate, descPredicate));
            }

            // Filter by status
            if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
                if ("active".equalsIgnoreCase(criteria.getStatus())) {
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.isTrue(root.get("isActive")));
                } else if ("inactive".equalsIgnoreCase(criteria.getStatus())) {
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.isFalse(root.get("isActive")));
                }
            }

            // Filter by price range
            if (criteria.getMinPrice() != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.greaterThanOrEqualTo(root.get("price"), criteria.getMinPrice()));
            }

            if (criteria.getMaxPrice() != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.lessThanOrEqualTo(root.get("price"), criteria.getMaxPrice()));
            }

            return predicate;
        };
    }

    /**
     * Build Sort object from sort parameters.
     *
     * @param sortBy  sort field
     * @param sortDir sort direction
     * @return Sort object
     */
    private Sort buildSort(String sortBy, String sortDir) {
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDir)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(direction, sortBy);
    }
}
