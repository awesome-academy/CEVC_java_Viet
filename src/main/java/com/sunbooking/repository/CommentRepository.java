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

import com.sunbooking.entity.Comment;

/**
 * Repository interface for Comment entity.
 * Provides CRUD operations and custom query methods for comment management.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {

    /**
     * Find an active comment by ID.
     *
     * @param id the comment ID
     * @return an Optional containing the comment if found and active, or empty if
     *         not found
     */
    @Query("SELECT c FROM Comment c WHERE c.id = :id AND c.isActive = true")
    Optional<Comment> findActiveById(@Param("id") Long id);

    /**
     * Find all active comments.
     *
     * @param pageable pagination information
     * @return a page of active comments
     */
    Page<Comment> findByIsActive(Boolean isActive, Pageable pageable);

    /**
     * Find comments by user.
     *
     * @param userId   the user ID
     * @param pageable pagination information
     * @return a page of comments by the user
     */
    Page<Comment> findByUserId(Long userId, Pageable pageable);

    /**
     * Find active comments by user.
     *
     * @param userId   the user ID
     * @param pageable pagination information
     * @return a page of active comments by the user
     */
    @Query("SELECT c FROM Comment c WHERE c.user.id = :userId AND c.isActive = true")
    Page<Comment> findActiveByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Find comments by review.
     *
     * @param reviewId the review ID
     * @param pageable pagination information
     * @return a page of comments on the review
     */
    Page<Comment> findByReviewId(Long reviewId, Pageable pageable);

    /**
     * Find active comments by review.
     *
     * @param reviewId the review ID
     * @param pageable pagination information
     * @return a page of active comments on the review
     */
    @Query("SELECT c FROM Comment c WHERE c.review.id = :reviewId AND c.isActive = true")
    Page<Comment> findActiveByReviewId(@Param("reviewId") Long reviewId, Pageable pageable);

    /**
     * Find top-level comments for a review (comments without parent).
     *
     * @param reviewId the review ID
     * @param pageable pagination information
     * @return a page of top-level comments
     */
    @Query("SELECT c FROM Comment c WHERE c.review.id = :reviewId AND c.parentComment IS NULL AND c.isActive = true")
    Page<Comment> findTopLevelCommentsByReviewId(@Param("reviewId") Long reviewId, Pageable pageable);

    /**
     * Find replies to a comment (comments with a specific parent).
     *
     * @param parentCommentId the parent comment ID
     * @param pageable        pagination information
     * @return a page of reply comments
     */
    @Query("SELECT c FROM Comment c WHERE c.parentComment.id = :parentCommentId AND c.isActive = true")
    Page<Comment> findRepliesByParentCommentId(@Param("parentCommentId") Long parentCommentId, Pageable pageable);

    /**
     * Find all replies to a comment (without pagination).
     *
     * @param parentCommentId the parent comment ID
     * @return a list of reply comments
     */
    @Query("SELECT c FROM Comment c WHERE c.parentComment.id = :parentCommentId AND c.isActive = true " +
            "ORDER BY c.createdAt ASC")
    List<Comment> findAllRepliesByParentCommentId(@Param("parentCommentId") Long parentCommentId);

    /**
     * Count comments by review.
     *
     * @param reviewId the review ID
     * @return the number of comments on the review
     */
    long countByReviewId(Long reviewId);

    /**
     * Count active comments by review.
     *
     * @param reviewId the review ID
     * @return the number of active comments on the review
     */
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.review.id = :reviewId AND c.isActive = true")
    long countActiveByReviewId(@Param("reviewId") Long reviewId);

    /**
     * Count replies to a comment.
     *
     * @param parentCommentId the parent comment ID
     * @return the number of replies to the comment
     */
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.parentComment.id = :parentCommentId AND c.isActive = true")
    long countRepliesByParentCommentId(@Param("parentCommentId") Long parentCommentId);

    /**
     * Count comments by user.
     *
     * @param userId the user ID
     * @return the number of comments by the user
     */
    long countByUserId(Long userId);

    /**
     * Count active comments by user.
     *
     * @param userId the user ID
     * @return the number of active comments by the user
     */
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.user.id = :userId AND c.isActive = true")
    long countActiveByUserId(@Param("userId") Long userId);

    /**
     * Find recent comments for a review.
     *
     * @param reviewId the review ID
     * @param pageable pagination information
     * @return a page of recent comments
     */
    @Query("SELECT c FROM Comment c WHERE c.review.id = :reviewId AND c.isActive = true " +
            "ORDER BY c.createdAt DESC")
    Page<Comment> findRecentCommentsByReviewId(@Param("reviewId") Long reviewId, Pageable pageable);

    /**
     * Check if a comment has any replies.
     *
     * @param commentId the comment ID
     * @return true if the comment has replies, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Comment c " +
            "WHERE c.parentComment.id = :commentId AND c.isActive = true")
    boolean hasReplies(@Param("commentId") Long commentId);
}
