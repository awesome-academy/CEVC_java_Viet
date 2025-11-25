package com.sunbooking.controller.admin;

import static com.sunbooking.constant.ViewConstants.ADMIN_REVIEWS_DETAIL;
import static com.sunbooking.constant.ViewConstants.ADMIN_REVIEWS_LIST;
import static com.sunbooking.constant.ViewConstants.REDIRECT_ADMIN_REVIEWS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sunbooking.dto.admin.review.ReviewDetailDTO;
import com.sunbooking.dto.admin.review.ReviewListDTO;
import com.sunbooking.dto.admin.review.ReviewSearchCriteria;
import com.sunbooking.entity.CategoryType;
import com.sunbooking.service.admin.ReviewManagementService;

/**
 * Controller for managing reviews in admin panel.
 * Handles listing, viewing, soft-delete and restore operations for reviews and
 * comments.
 */
@Controller
@RequestMapping("/admin/reviews")
public class ReviewManagementController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewManagementController.class);

    @Autowired
    private ReviewManagementService reviewManagementService;

    @Autowired
    private MessageSource messageSource;

    /**
     * Display review list with search and filter options.
     */
    @GetMapping
    public String listReviews(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) CategoryType categoryType,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            Model model) {

        logger.debug("Listing reviews - keyword: {}, categoryType: {}, isActive: {}, page: {}",
                keyword, categoryType, isActive, page);

        ReviewSearchCriteria criteria = ReviewSearchCriteria.builder()
                .keyword(keyword)
                .categoryType(categoryType)
                .isActive(isActive)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();

        Page<ReviewListDTO> reviews = reviewManagementService.getAllReviews(criteria);

        model.addAttribute("reviews", reviews);
        model.addAttribute("criteria", criteria);
        model.addAttribute("categoryTypes", CategoryType.values());

        return ADMIN_REVIEWS_LIST;
    }

    /**
     * Display review detail with comments.
     */
    @GetMapping("/{id}")
    public String viewReviewDetail(@PathVariable Long id, Model model) {
        logger.debug("Viewing review detail - id: {}", id);

        ReviewDetailDTO review = reviewManagementService.getReviewDetailById(id);
        model.addAttribute("review", review);

        return ADMIN_REVIEWS_DETAIL;
    }

    /**
     * Soft delete a review.
     */
    @PostMapping("/{id}/delete")
    public String softDeleteReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Soft deleting review - id: {}", id);

        try {
            reviewManagementService.softDeleteReview(id);
            addSuccessMessage(redirectAttributes, "success.review.deleted");
        } catch (Exception e) {
            logger.error("Error deleting review {}: {}", id, e.getMessage());
            addErrorMessage(redirectAttributes, e.getMessage());
        }

        return REDIRECT_ADMIN_REVIEWS;
    }

    /**
     * Restore a soft-deleted review.
     */
    @PostMapping("/{id}/restore")
    public String restoreReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Restoring review - id: {}", id);

        try {
            reviewManagementService.restoreReview(id);
            addSuccessMessage(redirectAttributes, "success.review.restored");
        } catch (Exception e) {
            logger.error("Error restoring review {}: {}", id, e.getMessage());
            addErrorMessage(redirectAttributes, e.getMessage());
        }

        return REDIRECT_ADMIN_REVIEWS;
    }

    /**
     * Soft delete a comment.
     */
    @PostMapping("/comments/{commentId}/delete")
    public String softDeleteComment(
            @PathVariable Long commentId,
            @RequestParam Long reviewId,
            RedirectAttributes redirectAttributes) {

        logger.info("Soft deleting comment - id: {}", commentId);

        try {
            reviewManagementService.softDeleteComment(commentId);
            addSuccessMessage(redirectAttributes, "success.comment.deleted");
        } catch (Exception e) {
            logger.error("Error deleting comment {}: {}", commentId, e.getMessage());
            addErrorMessage(redirectAttributes, e.getMessage());
        }

        return "redirect:/admin/reviews/" + reviewId;
    }

    /**
     * Restore a soft-deleted comment.
     */
    @PostMapping("/comments/{commentId}/restore")
    public String restoreComment(
            @PathVariable Long commentId,
            @RequestParam Long reviewId,
            RedirectAttributes redirectAttributes) {

        logger.info("Restoring comment - id: {}", commentId);

        try {
            reviewManagementService.restoreComment(commentId);
            addSuccessMessage(redirectAttributes, "success.comment.restored");
        } catch (Exception e) {
            logger.error("Error restoring comment {}: {}", commentId, e.getMessage());
            addErrorMessage(redirectAttributes, e.getMessage());
        }

        return "redirect:/admin/reviews/" + reviewId;
    }

    /**
     * Helper method to add success message using MessageSource.
     */
    private void addSuccessMessage(RedirectAttributes redirectAttributes, String messageKey) {
        String message = messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", message);
    }

    /**
     * Helper method to add error message.
     */
    private void addErrorMessage(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("errorMessage", message);
    }
}
