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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sunbooking.dto.admin.admin.AdminDTO;
import com.sunbooking.dto.admin.admin.AdminForm;
import com.sunbooking.dto.admin.admin.AdminListDTO;
import com.sunbooking.dto.admin.admin.AdminSearchCriteria;
import com.sunbooking.entity.User;
import com.sunbooking.entity.UserRole;
import com.sunbooking.exception.DuplicateResourceException;
import com.sunbooking.exception.ResourceNotFoundException;
import com.sunbooking.mapper.AdminMapper;
import com.sunbooking.repository.UserRepository;
import com.sunbooking.specification.UserSpecification;

/**
 * Service for managing admin accounts in the admin panel.
 * Handles CRUD operations with security restrictions:
 * - Self-service prevention (cannot delete/deactivate own account)
 * - Last-admin protection (cannot delete last active admin)
 * - Audit logging for all admin actions
 */
@Service
@Transactional
public class AdminManagementService {

    private static final Logger logger = LoggerFactory.getLogger(AdminManagementService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AdminMapper adminMapper;

    /**
     * Get paginated list of admins with search/filter criteria.
     *
     * @param criteria the search criteria
     * @return page of admin list DTOs
     */
    @Transactional(readOnly = true)
    public Page<AdminListDTO> getAllAdmins(AdminSearchCriteria criteria) {
        logger.debug("Fetching admins with criteria: {}", criteria);

        Pageable pageable = createPageable(criteria);

        // Build specification: only ADMIN role + search criteria
        Specification<User> spec = Specification.where(UserSpecification.hasRole(UserRole.ADMIN));

        if (criteria.hasKeyword()) {
            spec = spec.and(UserSpecification.nameOrEmailContains(criteria.getTrimmedKeyword()));
        }

        if (criteria.hasActiveFilter()) {
            if (criteria.getIsActive()) {
                spec = spec.and(UserSpecification.isActive());
            } else {
                spec = spec.and(UserSpecification.isInactive());
            }
        }

        Page<User> admins = userRepository.findAll(spec, pageable);
        logger.debug("Found {} admins", admins.getTotalElements());

        return admins.map(AdminListDTO::fromEntity);
    }

    /**
     * Get admin by ID with detailed information.
     *
     * @param id the admin ID
     * @return admin DTO
     * @throws ResourceNotFoundException if admin not found
     */
    @Transactional(readOnly = true)
    public AdminDTO getAdminById(Long id) {
        logger.debug("Fetching admin by ID: {}", id);

        User admin = userRepository.findById(id)
                .filter(user -> user.getRole() == UserRole.ADMIN)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("error.admin.not.found",
                            new Object[] { id }, LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });

        logger.debug("Found admin: {}", admin.getEmail());
        return adminMapper.toDTO(admin);
    }

    /**
     * Create a new admin account.
     *
     * @param form the admin form
     * @return created admin DTO
     * @throws DuplicateResourceException if email already exists
     * @throws IllegalArgumentException   if passwords don't match
     */
    public AdminDTO createAdmin(AdminForm form) {
        logger.debug("Creating new admin: {}", form.getEmail());

        // Validate email uniqueness
        if (userRepository.existsByEmail(form.getEmail())) {
            String message = messageSource.getMessage("error.admin.email.exists",
                    new Object[] { form.getEmail() }, LocaleContextHolder.getLocale());
            throw new DuplicateResourceException(message, "Admin", "email", form.getEmail());
        }

        // Validate password
        if (form.getPassword() == null || form.getPassword().trim().isEmpty()) {
            String message = messageSource.getMessage("validation.password.required",
                    null, LocaleContextHolder.getLocale());
            throw new IllegalArgumentException(message);
        }

        // Password match validation is now handled by @PasswordMatches annotation
        // Create admin using mapper
        User admin = adminMapper.toEntity(form);
        admin.setPassword(passwordEncoder.encode(form.getPassword()));
        admin.setRole(UserRole.ADMIN); // Ensure ADMIN role

        User savedAdmin = userRepository.save(admin);
        logger.info("Created new admin with ID: {}, Email: {} by user: {}",
                savedAdmin.getId(), savedAdmin.getEmail(), getCurrentUsername());

        return adminMapper.toDTO(savedAdmin);
    }

    /**
     * Update existing admin account.
     *
     * @param id   the admin ID
     * @param form the admin form
     * @return updated admin DTO
     * @throws ResourceNotFoundException  if admin not found
     * @throws DuplicateResourceException if email already exists
     * @throws IllegalStateException      if trying to deactivate own account or
     *                                    last
     *                                    admin
     */
    public AdminDTO updateAdmin(Long id, AdminForm form) {
        logger.debug("Updating admin ID: {}", id);

        User admin = userRepository.findById(id)
                .filter(user -> user.getRole() == UserRole.ADMIN)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("error.admin.not.found",
                            new Object[] { id }, LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });

        // Check email uniqueness (if email changed)
        if (!admin.getEmail().equals(form.getEmail())) {
            if (userRepository.existsByEmail(form.getEmail())) {
                String message = messageSource.getMessage("error.admin.email.exists",
                        new Object[] { form.getEmail() }, LocaleContextHolder.getLocale());
                throw new DuplicateResourceException(message, "Admin", "email", form.getEmail());
            }
        }

        // Self-service prevention: cannot deactivate own account
        if (isCurrentUser(admin.getEmail()) && !form.getIsActive()) {
            String message = messageSource.getMessage("error.admin.cannot.deactivate.self",
                    null, LocaleContextHolder.getLocale());
            throw new IllegalStateException(message);
        }

        // Last-admin protection: cannot deactivate last active admin
        if (admin.getIsActive() && !form.getIsActive()) {
            long activeAdminCount = userRepository.countByRoleAndIsActive(UserRole.ADMIN, true);
            if (activeAdminCount <= 1) {
                String message = messageSource.getMessage("error.admin.cannot.deactivate.last",
                        null, LocaleContextHolder.getLocale());
                throw new IllegalStateException(message);
            }
        }

        // Update admin fields using mapper
        adminMapper.updateEntityFromForm(form, admin);

        // Update password if provided
        if (form.getPassword() != null && !form.getPassword().trim().isEmpty()) {
            // Password match validation is now handled by @PasswordMatches annotation
            admin.setPassword(passwordEncoder.encode(form.getPassword()));
        }

        User updatedAdmin = userRepository.save(admin);
        logger.info("Updated admin with ID: {}, Email: {} by user: {}",
                updatedAdmin.getId(), updatedAdmin.getEmail(), getCurrentUsername());

        return adminMapper.toDTO(updatedAdmin);
    }

    /**
     * Soft delete an admin account.
     *
     * @param id the admin ID
     * @throws ResourceNotFoundException if admin not found
     * @throws IllegalStateException     if trying to delete own account or last
     *                                   admin
     */
    public void deleteAdmin(Long id) {
        logger.debug("Soft deleting admin ID: {}", id);

        User admin = userRepository.findById(id)
                .filter(user -> user.getRole() == UserRole.ADMIN)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("error.admin.not.found",
                            new Object[] { id }, LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });

        // Self-service prevention: cannot delete own account
        if (isCurrentUser(admin.getEmail())) {
            String message = messageSource.getMessage("error.admin.cannot.delete.self",
                    null, LocaleContextHolder.getLocale());
            throw new IllegalStateException(message);
        }

        // Last-admin protection: cannot delete last active admin
        if (admin.getIsActive()) {
            long activeAdminCount = userRepository.countByRoleAndIsActive(UserRole.ADMIN, true);
            if (activeAdminCount <= 1) {
                String message = messageSource.getMessage("error.admin.cannot.delete.last",
                        null, LocaleContextHolder.getLocale());
                throw new IllegalStateException(message);
            }
        }

        admin.softDelete();
        userRepository.save(admin);

        logger.info("Soft deleted admin with ID: {}, Email: {} by user: {}",
                admin.getId(), admin.getEmail(), getCurrentUsername());
    }

    /**
     * Reactivate a soft-deleted admin account.
     *
     * @param id the admin ID
     * @throws ResourceNotFoundException if admin not found
     */
    public void activateAdmin(Long id) {
        logger.debug("Reactivating admin ID: {}", id);

        User admin = userRepository.findById(id)
                .filter(user -> user.getRole() == UserRole.ADMIN)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("error.admin.not.found",
                            new Object[] { id }, LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });

        admin.restore();
        userRepository.save(admin);

        logger.info("Reactivated admin with ID: {}, Email: {} by user: {}",
                admin.getId(), admin.getEmail(), getCurrentUsername());
    }

    /**
     * Get admin statistics.
     *
     * @return statistics object
     */
    @Transactional(readOnly = true)
    public AdminStatistics getStatistics() {
        long totalAdmins = userRepository.countByRole(UserRole.ADMIN);
        long activeAdmins = userRepository.countByRoleAndIsActive(UserRole.ADMIN, true);
        long inactiveAdmins = userRepository.countByRoleAndIsActive(UserRole.ADMIN, false);

        return new AdminStatistics(totalAdmins, activeAdmins, inactiveAdmins);
    }

    /**
     * Check if the given email belongs to the currently logged-in user.
     */
    private boolean isCurrentUser(String email) {
        String currentUsername = getCurrentUsername();
        return currentUsername != null && currentUsername.equals(email);
    }

    /**
     * Get current authenticated username (email).
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    /**
     * Create pageable object from search criteria.
     */
    private Pageable createPageable(AdminSearchCriteria criteria) {
        Sort sort = Sort.by(Sort.Direction.fromString(criteria.getSortDir()), criteria.getSortBy());
        return PageRequest.of(criteria.getPage(), criteria.getSize(), sort);
    }

    /**
     * Inner class for admin statistics.
     */
    public static class AdminStatistics {
        private final long totalAdmins;
        private final long activeAdmins;
        private final long inactiveAdmins;

        public AdminStatistics(long totalAdmins, long activeAdmins, long inactiveAdmins) {
            this.totalAdmins = totalAdmins;
            this.activeAdmins = activeAdmins;
            this.inactiveAdmins = inactiveAdmins;
        }

        public long getTotalAdmins() {
            return totalAdmins;
        }

        public long getActiveAdmins() {
            return activeAdmins;
        }

        public long getInactiveAdmins() {
            return inactiveAdmins;
        }
    }
}
