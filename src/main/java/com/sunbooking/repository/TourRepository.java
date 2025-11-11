package com.sunbooking.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunbooking.entity.Tour;

/**
 * Repository interface for Tour entity.
 * Provides CRUD operations and custom query methods for tour management.
 */
@Repository
public interface TourRepository extends JpaRepository<Tour, Long>, JpaSpecificationExecutor<Tour> {

    /**
     * Find an active tour by ID.
     *
     * @param id the tour ID
     * @return an Optional containing the tour if found and active, or empty if not
     *         found
     */
    @Query("SELECT t FROM Tour t WHERE t.id = :id AND t.isActive = true")
    Optional<Tour> findActiveById(@Param("id") Long id);

    /**
     * Find all active tours.
     *
     * @param pageable pagination information
     * @return a page of active tours
     */
    Page<Tour> findByIsActive(Boolean isActive, Pageable pageable);

    /**
     * Search tours by title or description (case-insensitive).
     *
     * @param keyword  the search keyword
     * @param pageable pagination information
     * @return a page of tours matching the search criteria
     */
    @Query("SELECT t FROM Tour t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Tour> searchByTitleOrDescription(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Search active tours by title or description (case-insensitive).
     *
     * @param keyword  the search keyword
     * @param pageable pagination information
     * @return a page of active tours matching the search criteria
     */
    @Query("SELECT t FROM Tour t WHERE (LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND t.isActive = true")
    Page<Tour> searchActiveTours(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Find tours within a price range.
     *
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @param pageable pagination information
     * @return a page of tours within the price range
     */
    Page<Tour> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * Find active tours within a price range.
     *
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @param pageable pagination information
     * @return a page of active tours within the price range
     */
    @Query("SELECT t FROM Tour t WHERE t.price BETWEEN :minPrice AND :maxPrice AND t.isActive = true")
    Page<Tour> findActiveByPriceBetween(@Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);

    /**
     * Search active tours by keyword and price range.
     *
     * @param keyword  the search keyword
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @param pageable pagination information
     * @return a page of active tours matching the criteria
     */
    @Query("SELECT t FROM Tour t WHERE (LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND t.price BETWEEN :minPrice AND :maxPrice AND t.isActive = true")
    Page<Tour> searchActiveToursWithPriceRange(@Param("keyword") String keyword,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);

    /**
     * Find tours ordered by price (ascending).
     *
     * @param isActive the active status
     * @param pageable pagination information
     * @return a page of tours ordered by price
     */
    Page<Tour> findByIsActiveOrderByPriceAsc(Boolean isActive, Pageable pageable);

    /**
     * Find tours ordered by price (descending).
     *
     * @param isActive the active status
     * @param pageable pagination information
     * @return a page of tours ordered by price
     */
    Page<Tour> findByIsActiveOrderByPriceDesc(Boolean isActive, Pageable pageable);

    /**
     * Get tour statistics: total bookings and total revenue.
     *
     * @param tourId the tour ID
     * @return an array containing [totalBookings, totalRevenue]
     */
    @Query("SELECT COUNT(b), COALESCE(SUM(t.price), 0) FROM Booking b " +
            "JOIN b.tour t WHERE t.id = :tourId")
    Object[] getTourStatistics(@Param("tourId") Long tourId);

    /**
     * Get average rating for a tour.
     *
     * @param tourId the tour ID
     * @return the average rating or null if no ratings
     */
    @Query("SELECT AVG(b.rating) FROM Booking b WHERE b.tour.id = :tourId AND b.rating IS NOT NULL")
    Double getAverageRating(@Param("tourId") Long tourId);

    /**
     * Check if a tour has any pending or confirmed bookings.
     *
     * @param tourId the tour ID
     * @return true if the tour has active bookings, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b " +
            "WHERE b.tour.id = :tourId AND b.status IN ('PENDING', 'CONFIRMED')")
    boolean hasActiveBookings(@Param("tourId") Long tourId);

    /**
     * Count active tours.
     *
     * @return the number of active tours
     */
    long countByIsActive(Boolean isActive);

    /**
     * Find top rated tours.
     *
     * @param limit the maximum number of results
     * @return a list of top rated tours
     */
    @Query("SELECT t FROM Tour t LEFT JOIN t.bookings b " +
            "WHERE t.isActive = true GROUP BY t.id ORDER BY AVG(b.rating) DESC")
    List<Tour> findTopRatedTours(Pageable pageable);
}
