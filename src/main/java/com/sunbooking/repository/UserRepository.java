package com.sunbooking.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunbooking.entity.User;
import com.sunbooking.entity.UserRole;

/**
 * Repository interface for User entity.
 * Provides CRUD operations and custom query methods for user management.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * Find a user by email address.
     *
     * @param email the email address to search for
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<User> findByEmail(String email);

    /**
     * Find an active user by email address.
     *
     * @param email    the email address to search for
     * @param isActive the active status (should be true for active users)
     * @return an Optional containing the user if found and active, or empty if not
     *         found
     */
    Optional<User> findByEmailAndIsActive(String email, Boolean isActive);

    /**
     * Check if a user with the given email exists.
     *
     * @param email the email address to check
     * @return true if a user with this email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find all users by role.
     *
     * @param role     the user role to filter by
     * @param pageable pagination information
     * @return a page of users with the specified role
     */
    Page<User> findByRole(UserRole role, Pageable pageable);

    /**
     * Find all active users by role.
     *
     * @param role     the user role to filter by
     * @param isActive the active status (should be true for active users)
     * @param pageable pagination information
     * @return a page of active users with the specified role
     */
    Page<User> findByRoleAndIsActive(UserRole role, Boolean isActive, Pageable pageable);

    /**
     * Find all users by active status.
     *
     * @param isActive the active status
     * @param pageable pagination information
     * @return a page of users with the specified active status
     */
    Page<User> findByIsActive(Boolean isActive, Pageable pageable);

    /**
     * Search users by name or email (case-insensitive).
     *
     * @param keyword  the search keyword
     * @param pageable pagination information
     * @return a page of users matching the search criteria
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<User> searchByNameOrEmail(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Search active users by name or email (case-insensitive).
     *
     * @param keyword  the search keyword
     * @param isActive the active status (should be true for active users)
     * @param pageable pagination information
     * @return a page of active users matching the search criteria
     */
    @Query("SELECT u FROM User u WHERE (LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND u.isActive = :isActive")
    Page<User> searchByNameOrEmailAndIsActive(@Param("keyword") String keyword,
            @Param("isActive") Boolean isActive,
            Pageable pageable);

    /**
     * Count users by role.
     *
     * @param role the user role
     * @return the number of users with the specified role
     */
    long countByRole(UserRole role);

    /**
     * Count active users by role.
     *
     * @param role     the user role
     * @param isActive the active status (should be true for active users)
     * @return the number of active users with the specified role
     */
    long countByRoleAndIsActive(UserRole role, Boolean isActive);

    /**
     * Count users by active status.
     *
     * @param isActive the active status
     * @return the number of users with the specified active status
     */
    long countByIsActive(Boolean isActive);
}
