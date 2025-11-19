package com.sunbooking.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunbooking.entity.CategoryType;
import com.sunbooking.entity.Review;

/**
 * Repository interface for Review entity.
 * Provides CRUD operations and custom query methods for review management.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {

        /**
         * Find an active review by ID.
         *
         * @param id the review ID
         * @return an Optional containing the review if found and active, or empty if
         *         not found
         */
        @Query("SELECT r FROM Review r WHERE r.id = :id AND r.isActive = true")
        Optional<Review> findActiveById(@Param("id") Long id);

        /**
         * Find all active reviews.
         *
         * @param pageable pagination information
         * @return a page of active reviews
         */
        Page<Review> findByIsActive(Boolean isActive, Pageable pageable);

        /**
         * Find reviews by user.
         *
         * @param userId   the user ID
         * @param pageable pagination information
         * @return a page of reviews by the user
         */
        Page<Review> findByUserId(Long userId, Pageable pageable);

        /**
         * Find active reviews by user.
         *
         * @param userId   the user ID
         * @param pageable pagination information
         * @return a page of active reviews by the user
         */
        @Query("SELECT r FROM Review r WHERE r.user.id = :userId AND r.isActive = true")
        Page<Review> findActiveByUserId(@Param("userId") Long userId, Pageable pageable);

        /**
         * Find reviews by category.
         *
         * @param categoryId the category ID
         * @param pageable   pagination information
         * @return a page of reviews in the category
         */
        Page<Review> findByCategoryId(Long categoryId, Pageable pageable);

        /**
         * Find active reviews by category.
         *
         * @param categoryId the category ID
         * @param pageable   pagination information
         * @return a page of active reviews in the category
         */
        @Query("SELECT r FROM Review r WHERE r.category.id = :categoryId AND r.isActive = true")
        Page<Review> findActiveByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

        /**
         * Find reviews by category type.
         *
         * @param categoryType the category type
         * @param pageable     pagination information
         * @return a page of reviews with the specified category type
         */
        @Query("SELECT r FROM Review r WHERE r.category.type = :categoryType")
        Page<Review> findByCategoryType(@Param("categoryType") CategoryType categoryType, Pageable pageable);

        /**
         * Find active reviews by category type.
         *
         * @param categoryType the category type
         * @param pageable     pagination information
         * @return a page of active reviews with the specified category type
         */
        @Query("SELECT r FROM Review r WHERE r.category.type = :categoryType AND r.isActive = true")
        Page<Review> findActiveByCategoryType(@Param("categoryType") CategoryType categoryType, Pageable pageable);

        /**
         * Find reviews by tour.
         *
         * @param tourId   the tour ID
         * @param pageable pagination information
         * @return a page of reviews for the tour
         */
        Page<Review> findByTourId(Long tourId, Pageable pageable);

        /**
         * Find active reviews by tour.
         *
         * @param tourId   the tour ID
         * @param pageable pagination information
         * @return a page of active reviews for the tour
         */
        @Query("SELECT r FROM Review r WHERE r.tour.id = :tourId AND r.isActive = true")
        Page<Review> findActiveByTourId(@Param("tourId") Long tourId, Pageable pageable);

        /**
         * Search reviews by title or content (case-insensitive).
         *
         * @param keyword  the search keyword
         * @param pageable pagination information
         * @return a page of reviews matching the search criteria
         */
        @Query("SELECT r FROM Review r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                        "OR LOWER(r.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
        Page<Review> searchByTitleOrContent(@Param("keyword") String keyword, Pageable pageable);

        /**
         * Search active reviews by title or content (case-insensitive).
         *
         * @param keyword  the search keyword
         * @param pageable pagination information
         * @return a page of active reviews matching the search criteria
         */
        @Query("SELECT r FROM Review r WHERE (LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                        "OR LOWER(r.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND r.isActive = true")
        Page<Review> searchActiveReviews(@Param("keyword") String keyword, Pageable pageable);

        /**
         * Get review with comment count and like count.
         *
         * @param reviewId the review ID
         * @return an array containing [commentCount, likeCount]
         */
        @Query("SELECT " +
                        "(SELECT COUNT(c) FROM Comment c WHERE c.review.id = :reviewId AND c.isActive = true), " +
                        "(SELECT COUNT(l) FROM Like l WHERE l.review.id = :reviewId) " +
                        "FROM Review r WHERE r.id = :reviewId")
        Object[] getReviewStatistics(@Param("reviewId") Long reviewId);

        /**
         * Find recent active reviews.
         *
         * @param pageable pagination information
         * @return a page of recent active reviews
         */
        @Query("SELECT r FROM Review r WHERE r.isActive = true ORDER BY r.createdAt DESC")
        Page<Review> findRecentActiveReviews(Pageable pageable);

        /**
         * Find most liked reviews.
         *
         * @param pageable pagination information
         * @return a page of most liked reviews
         */
        @Query("SELECT r FROM Review r LEFT JOIN r.likes l WHERE r.isActive = true " +
                        "GROUP BY r.id ORDER BY COUNT(l) DESC")
        Page<Review> findMostLikedReviews(Pageable pageable);

        /**
         * Count reviews by user.
         *
         * @param userId the user ID
         * @return the number of reviews by the user
         */
        long countByUserId(Long userId);

        /**
         * Count active reviews by user.
         *
         * @param userId the user ID
         * @return the number of active reviews by the user
         */
        @Query("SELECT COUNT(r) FROM Review r WHERE r.user.id = :userId AND r.isActive = true")
        long countActiveByUserId(@Param("userId") Long userId);

        /**
         * Count reviews by category.
         *
         * @param categoryId the category ID
         * @return the number of reviews in the category
         */
        long countByCategoryId(Long categoryId);

        /**
         * Count active reviews by category.
         *
         * @param categoryId the category ID
         * @return the number of active reviews in the category
         */
        @Query("SELECT COUNT(r) FROM Review r WHERE r.category.id = :categoryId AND r.isActive = true")
        long countActiveByCategoryId(@Param("categoryId") Long categoryId);

        /**
         * Count active reviews.
         *
         * @return the number of active reviews
         */
        long countByIsActive(Boolean isActive);

        /**
         * Count reviews by tour ID.
         *
         * @param tourId the tour ID
         * @return the number of reviews for the tour
         */
        long countByTourId(Long tourId);
}
