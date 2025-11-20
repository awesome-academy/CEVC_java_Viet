package com.sunbooking.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.sunbooking.entity.Category;
import com.sunbooking.entity.CategoryType;

public class CategorySpecification {

    public static Specification<Category> hasType(CategoryType type) {
        return (Root<Category> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (type == null)
                return cb.conjunction();
            return cb.equal(root.get("type"), type);
        };
    }

    public static Specification<Category> nameContains(String keyword) {
        return (Root<Category> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (keyword == null || keyword.trim().isEmpty())
                return cb.conjunction();
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.like(cb.lower(root.get("name")), pattern);
        };
    }

    public static Specification<Category> isActive() {
        return (Root<Category> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<Category> isDeleted() {
        return (Root<Category> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb.isNotNull(root.get("deletedAt"));
    }

    public static Specification<Category> searchCategories(
            String keyword, CategoryType type, boolean showDeleted) {
        Specification<Category> spec = Specification.where(null);
        if (type != null)
            spec = spec.and(hasType(type));
        if (keyword != null && !keyword.trim().isEmpty())
            spec = spec.and(nameContains(keyword));
        if (!showDeleted)
            spec = spec.and(isActive());
        return spec;
    }
}
