package com.sunbooking.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import com.sunbooking.dto.admin.user.UserSearchCriteria;
import com.sunbooking.entity.User;
import com.sunbooking.entity.UserRole;

/**
 * JPA Specification for dynamic User queries.
 * Provides methods to build complex search criteria.
 */
public class UserSpecification {

    /**
     * Create specification based on search criteria.
     *
     * @param criteria the search criteria
     * @return the specification
     */
    public static Specification<User> withCriteria(UserSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Keyword search (name or email)
            if (criteria.hasKeyword()) {
                String likePattern = "%" + criteria.getTrimmedKeyword().toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")), likePattern);
                Predicate emailPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")), likePattern);
                predicates.add(criteriaBuilder.or(namePredicate, emailPredicate));
            }

            // Role filter
            if (criteria.hasRoleFilter()) {
                predicates.add(criteriaBuilder.equal(root.get("role"), criteria.getRole()));
            }

            // Active status filter
            if (criteria.hasActiveFilter()) {
                predicates.add(criteriaBuilder.equal(root.get("isActive"), criteria.getIsActive()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Specification for active users only.
     *
     * @return the specification
     */
    public static Specification<User> isActive() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isActive"), true);
    }

    /**
     * Specification for inactive users only.
     *
     * @return the specification
     */
    public static Specification<User> isInactive() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isActive"), false);
    }

    /**
     * Specification for users with specific role.
     *
     * @param role the user role
     * @return the specification
     */
    public static Specification<User> hasRole(UserRole role) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("role"), role);
    }

    /**
     * Specification for users with name or email containing keyword.
     *
     * @param keyword the search keyword
     * @return the specification
     */
    public static Specification<User> nameOrEmailContains(String keyword) {
        return (root, query, criteriaBuilder) -> {
            String likePattern = "%" + keyword.toLowerCase() + "%";
            Predicate namePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")), likePattern);
            Predicate emailPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("email")), likePattern);
            return criteriaBuilder.or(namePredicate, emailPredicate);
        };
    }
}
