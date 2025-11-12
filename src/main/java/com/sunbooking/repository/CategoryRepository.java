package com.sunbooking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunbooking.entity.Category;
import com.sunbooking.entity.CategoryType;

/**
 * Repository interface for Category entity.
 * Provides CRUD operations and custom query methods for category management.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    /**
     * Find a category by name and type.
     *
     * @param name the category name
     * @param type the category type
     * @return an Optional containing the category if found, or empty if not found
     */
    Optional<Category> findByNameAndType(String name, CategoryType type);

    /**
     * Find all categories by type.
     *
     * @param type the category type
     * @return a list of categories with the specified type
     */
    List<Category> findByType(CategoryType type);

    /**
     * Find all active categories (not soft-deleted) by type.
     *
     * @param type the category type
     * @return a list of active categories with the specified type
     */
    @Query("SELECT c FROM Category c WHERE c.type = :type AND c.deletedAt IS NULL")
    List<Category> findActiveByType(@Param("type") CategoryType type);

    /**
     * Find all active categories (not soft-deleted).
     *
     * @return a list of all active categories
     */
    @Query("SELECT c FROM Category c WHERE c.deletedAt IS NULL")
    List<Category> findAllActive();

    /**
     * Check if a category with the given name and type exists.
     *
     * @param name the category name
     * @param type the category type
     * @return true if a category with this name and type exists, false otherwise
     */
    boolean existsByNameAndType(String name, CategoryType type);

    /**
     * Check if a category exists by name, type, and excluding a specific ID.
     * Useful for update operations to check uniqueness.
     *
     * @param name the category name
     * @param type the category type
     * @param id   the ID to exclude from the check
     * @return true if a category with this name and type exists (excluding the
     *         given ID), false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category c " +
            "WHERE c.name = :name AND c.type = :type AND c.id <> :id")
    boolean existsByNameAndTypeAndIdNot(@Param("name") String name,
            @Param("type") CategoryType type,
            @Param("id") Long id);

    /**
     * Count categories by type.
     *
     * @param type the category type
     * @return the number of categories with the specified type
     */
    long countByType(CategoryType type);

    /**
     * Count active categories by type.
     *
     * @param type the category type
     * @return the number of active categories with the specified type
     */
    @Query("SELECT COUNT(c) FROM Category c WHERE c.type = :type AND c.deletedAt IS NULL")
    long countActiveByType(@Param("type") CategoryType type);

    /**
     * Check if a category has any associated active reviews.
     *
     * @param categoryId the category ID
     * @return true if the category has active reviews, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Review r " +
            "WHERE r.category.id = :categoryId AND r.isActive = true")
    boolean hasActiveReviews(@Param("categoryId") Long categoryId);
}
