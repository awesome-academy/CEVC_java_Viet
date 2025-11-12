package com.sunbooking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunbooking.entity.Like;

/**
 * Repository interface for Like entity.
 * Provides CRUD operations and custom query methods for like management.
 */
@Repository
public interface LikeRepository extends JpaRepository<Like, Long>, JpaSpecificationExecutor<Like> {

    /**
     * Find a like by user and review.
     *
     * @param userId   the user ID
     * @param reviewId the review ID
     * @return an Optional containing the like if found, or empty if not found
     */
    Optional<Like> findByUserIdAndReviewId(Long userId, Long reviewId);

    /**
     * Check if a user has liked a review.
     *
     * @param userId   the user ID
     * @param reviewId the review ID
     * @return true if the user has liked the review, false otherwise
     */
    boolean existsByUserIdAndReviewId(Long userId, Long reviewId);

    /**
     * Find all likes by a user.
     *
     * @param userId   the user ID
     * @param pageable pagination information
     * @return a page of likes by the user
     */
    Page<Like> findByUserId(Long userId, Pageable pageable);

    /**
     * Find all likes for a review.
     *
     * @param reviewId the review ID
     * @param pageable pagination information
     * @return a page of likes for the review
     */
    Page<Like> findByReviewId(Long reviewId, Pageable pageable);

    /**
     * Count likes by user.
     *
     * @param userId the user ID
     * @return the number of likes by the user
     */
    long countByUserId(Long userId);

    /**
     * Count likes for a review.
     *
     * @param reviewId the review ID
     * @return the number of likes for the review
     */
    long countByReviewId(Long reviewId);

    /**
     * Delete a like by user and review.
     * Used for toggling likes (unlike functionality).
     *
     * @param userId   the user ID
     * @param reviewId the review ID
     */
    void deleteByUserIdAndReviewId(Long userId, Long reviewId);

    /**
     * Find reviews liked by a user (ordered by most recent).
     *
     * @param userId   the user ID
     * @param pageable pagination information
     * @return a page of review IDs liked by the user
     */
    @Query("SELECT l.review.id FROM Like l WHERE l.user.id = :userId ORDER BY l.createdAt DESC")
    Page<Long> findReviewIdsLikedByUser(@Param("userId") Long userId, Pageable pageable);

    /**
     * Find users who liked a review.
     *
     * @param reviewId the review ID
     * @param pageable pagination information
     * @return a page of user IDs who liked the review
     */
    @Query("SELECT l.user.id FROM Like l WHERE l.review.id = :reviewId ORDER BY l.createdAt DESC")
    Page<Long> findUserIdsWhoLikedReview(@Param("reviewId") Long reviewId, Pageable pageable);

    /**
     * Get most liked reviews.
     *
     * @param pageable pagination information (use PageRequest.of(0, limit) for top
     *                 N)
     * @return a list of review IDs ordered by like count
     */
    @Query("SELECT l.review.id FROM Like l GROUP BY l.review.id ORDER BY COUNT(l) DESC")
    List<Long> findMostLikedReviewIds(Pageable pageable);

    /**
     * Get like count for multiple reviews.
     *
     * @param reviewIds the list of review IDs
     * @return a list of arrays containing [reviewId, likeCount]
     */
    @Query("SELECT l.review.id, COUNT(l) FROM Like l WHERE l.review.id IN :reviewIds GROUP BY l.review.id")
    List<Object[]> countLikesByReviewIds(@Param("reviewIds") List<Long> reviewIds);
}
