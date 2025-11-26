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

import com.sunbooking.dto.admin.review.ReviewDetailDTO;
import com.sunbooking.dto.admin.review.ReviewListDTO;
import com.sunbooking.dto.admin.review.ReviewSearchCriteria;
import com.sunbooking.entity.Comment;
import com.sunbooking.entity.Review;
import com.sunbooking.exception.ResourceNotFoundException;
import com.sunbooking.repository.CommentRepository;
import com.sunbooking.repository.ReviewRepository;
import com.sunbooking.specification.ReviewSpecification;
import com.sunbooking.util.ReviewValidator;

/**
 * Service for managing reviews in admin panel.
 * Handles review listing, filtering, viewing, soft-delete and restore
 * operations.
 */
@Service
@Transactional
public class ReviewManagementService {

        private static final Logger logger = LoggerFactory.getLogger(ReviewManagementService.class);

        @Autowired
        private ReviewRepository reviewRepository;

        @Autowired
        private CommentRepository commentRepository;

        @Autowired
        private MessageSource messageSource;

        @Autowired
        private ReviewValidator reviewValidator;

        /**
         * Get all reviews with search and filter criteria.
         *
         * @param criteria the search criteria
         * @return page of review list DTOs
         */
        @Transactional(readOnly = true)
        public Page<ReviewListDTO> getAllReviews(ReviewSearchCriteria criteria) {
                logger.debug("Getting all reviews with criteria: {}", criteria);

                Sort sort = Sort.by(
                                "DESC".equalsIgnoreCase(criteria.getSortDir()) ? Sort.Direction.DESC
                                                : Sort.Direction.ASC,
                                criteria.getSortBy());

                Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize(), sort);
                Specification<Review> spec = ReviewSpecification.buildSpecification(criteria);

                return reviewRepository.findAll(spec, pageable)
                                .map(review -> {
                                        reviewValidator.validateReviewIntegrity(review);
                                        return ReviewListDTO.fromEntity(review);
                                });
        }

        /**
         * Get detailed review information by ID.
         *
         * @param id the review ID
         * @return detailed review DTO if found
         */
        @Transactional(readOnly = true)
        public ReviewDetailDTO getReviewDetailById(Long id) {
                logger.debug("Getting review detail by id: {}", id);

                Review review = reviewRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                messageSource.getMessage("error.review.not.found",
                                                                new Object[] { id },
                                                                LocaleContextHolder.getLocale())));

                reviewValidator.validateReviewIntegrity(review);
                return ReviewDetailDTO.fromEntity(review);
        }

        /**
         * Soft delete a review.
         *
         * @param id the review ID to delete
         */
        @Transactional
        public void softDeleteReview(Long id) {
                logger.info("Soft deleting review with id: {}", id);

                Review review = reviewRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                messageSource.getMessage("error.review.not.found",
                                                                new Object[] { id },
                                                                LocaleContextHolder.getLocale())));

                if (!review.getIsActive()) {
                        throw new IllegalStateException(
                                        messageSource.getMessage("error.review.already.deleted",
                                                        new Object[] { id },
                                                        LocaleContextHolder.getLocale()));
                }

                review.softDelete();
                reviewRepository.save(review);

                logger.info("Review {} soft deleted successfully", id);
        }

        /**
         * Restore a soft-deleted review.
         *
         * @param id the review ID to restore
         */
        @Transactional
        public void restoreReview(Long id) {
                logger.info("Restoring review with id: {}", id);

                Review review = reviewRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                messageSource.getMessage("error.review.not.found",
                                                                new Object[] { id },
                                                                LocaleContextHolder.getLocale())));

                if (review.getIsActive()) {
                        throw new IllegalStateException(
                                        messageSource.getMessage("error.review.not.deleted",
                                                        new Object[] { id },
                                                        LocaleContextHolder.getLocale()));
                }

                review.restore();
                reviewRepository.save(review);

                logger.info("Review {} restored successfully", id);
        }

        /**
         * Soft delete a comment.
         *
         * @param commentId the comment ID to delete
         */
        @Transactional
        public void softDeleteComment(Long commentId) {
                logger.info("Soft deleting comment with id: {}", commentId);

                Comment comment = commentRepository.findById(commentId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                messageSource.getMessage("error.comment.not.found",
                                                                new Object[] { commentId },
                                                                LocaleContextHolder.getLocale())));

                if (!comment.getIsActive()) {
                        throw new IllegalStateException(
                                        messageSource.getMessage("error.comment.already.deleted",
                                                        new Object[] { commentId },
                                                        LocaleContextHolder.getLocale()));
                }

                comment.softDelete();
                commentRepository.save(comment);

                logger.info("Comment {} soft deleted successfully", commentId);
        }

        /**
         * Restore a soft-deleted comment.
         *
         * @param commentId the comment ID to restore
         */
        @Transactional
        public void restoreComment(Long commentId) {
                logger.info("Restoring comment with id: {}", commentId);

                Comment comment = commentRepository.findById(commentId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                messageSource.getMessage("error.comment.not.found",
                                                                new Object[] { commentId },
                                                                LocaleContextHolder.getLocale())));

                if (comment.getIsActive()) {
                        throw new IllegalStateException(
                                        messageSource.getMessage("error.comment.not.deleted",
                                                        new Object[] { commentId },
                                                        LocaleContextHolder.getLocale()));
                }

                comment.restore();
                commentRepository.save(comment);

                logger.info("Comment {} restored successfully", commentId);
        }
}
