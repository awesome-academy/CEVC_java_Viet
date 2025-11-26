package com.sunbooking.specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import com.sunbooking.dto.admin.review.ReviewSearchCriteria;
import com.sunbooking.entity.Category;
import com.sunbooking.entity.CategoryType;
import com.sunbooking.entity.Review;
import com.sunbooking.entity.User;

/**
 * Utility class for building JPA Specifications for Review queries.
 * Used to dynamically construct queries based on search criteria.
 */
public class ReviewSpecification {

    private static final Logger logger = LoggerFactory.getLogger(ReviewSpecification.class);

    private ReviewSpecification() {
        // Private constructor to prevent instantiation
    }

    /**
     * Build a Specification for Review based on search criteria.
     *
     * @param criteria the search criteria
     * @return Specification for Review
     */
    public static Specification<Review> buildSpecification(ReviewSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // Join with User and Category for search
            Join<Review, User> userJoin = root.join("user", JoinType.LEFT);
            Join<Review, Category> categoryJoin = root.join("category", JoinType.LEFT);

            // Keyword search (title, content, user name, user email)
            if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
                String keyword = "%" + criteria.getKeyword().trim().toLowerCase() + "%";
                Predicate keywordPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), keyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("content")), keyword),
                        criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("name")), keyword),
                        criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("email")), keyword));
                predicate = criteriaBuilder.and(predicate, keywordPredicate);
                logger.debug("Applied keyword filter: {}", criteria.getKeyword());
            }

            // Category type filter
            if (criteria.getCategoryType() != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(categoryJoin.get("type"), criteria.getCategoryType()));
                logger.debug("Applied category type filter: {}", criteria.getCategoryType());
            }

            // Active status filter
            if (criteria.getIsActive() != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("isActive"), criteria.getIsActive()));
                logger.debug("Applied active status filter: {}", criteria.getIsActive());
            }

            return predicate;
        };
    }

    /**
     * Specification for reviews by user ID.
     */
    public static Specification<Review> hasUserId(Long userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Review, User> userJoin = root.join("user", JoinType.INNER);
            return criteriaBuilder.equal(userJoin.get("id"), userId);
        };
    }

    /**
     * Specification for reviews by category ID.
     */
    public static Specification<Review> hasCategoryId(Long categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Review, Category> categoryJoin = root.join("category", JoinType.INNER);
            return criteriaBuilder.equal(categoryJoin.get("id"), categoryId);
        };
    }

    /**
     * Specification for reviews by category type.
     */
    public static Specification<Review> hasCategoryType(CategoryType categoryType) {
        return (root, query, criteriaBuilder) -> {
            if (categoryType == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Review, Category> categoryJoin = root.join("category", JoinType.INNER);
            return criteriaBuilder.equal(categoryJoin.get("type"), categoryType);
        };
    }

    /**
     * Specification for active reviews only.
     */
    public static Specification<Review> isActive() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("isActive"));
    }

    /**
     * Specification for deleted reviews only.
     */
    public static Specification<Review> isDeleted() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get("isActive"));
    }
}
