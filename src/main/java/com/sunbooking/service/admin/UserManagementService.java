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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sunbooking.dto.admin.user.UserDTO;
import com.sunbooking.dto.admin.user.UserForm;
import com.sunbooking.dto.admin.user.UserListDTO;
import com.sunbooking.dto.admin.user.UserSearchCriteria;
import com.sunbooking.entity.User;
import com.sunbooking.entity.UserRole;
import com.sunbooking.exception.DuplicateResourceException;
import com.sunbooking.exception.ResourceNotFoundException;
import com.sunbooking.mapper.UserMapper;
import com.sunbooking.repository.UserRepository;
import com.sunbooking.specification.UserSpecification;

/**
 * Service for managing users (USER role only) in the admin panel.
 * Handles CRUD operations, search, pagination, soft-delete, and reactivation.
 * Note: Admin accounts are managed in AdminManagementService.
 */
@Service
@Transactional
public class UserManagementService {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UserMapper userMapper;

    /**
     * Get paginated list of users (USER role only) with search/filter criteria.
     *
     * @param criteria the search criteria
     * @return page of user list DTOs
     */
    @Transactional(readOnly = true)
    public Page<UserListDTO> getAllUsers(UserSearchCriteria criteria) {
        logger.debug("Fetching users with criteria: {}", criteria);

        Pageable pageable = createPageable(criteria);

        // Build specification: only USER role + search criteria
        Specification<User> spec = Specification.where(UserSpecification.hasRole(UserRole.USER))
                .and(UserSpecification.withCriteria(criteria));

        Page<User> users = userRepository.findAll(spec, pageable);

        logger.debug("Found {} users", users.getTotalElements());
        return users.map(userMapper::toDTO).map(dto -> {
            UserListDTO listDTO = new UserListDTO();
            listDTO.setId(dto.getId());
            listDTO.setName(dto.getName());
            listDTO.setEmail(dto.getEmail());
            listDTO.setPhone(dto.getPhone());
            listDTO.setRole(dto.getRole());
            listDTO.setIsActive(dto.getIsActive());
            listDTO.setCreatedAt(dto.getCreatedAt());
            return listDTO;
        });
    }

    /**
     * Get user by ID with detailed information.
     * Only returns users with USER role.
     *
     * @param id the user ID
     * @return user DTO with statistics
     * @throws ResourceNotFoundException if user not found or not a USER
     */
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        logger.debug("Fetching user by ID: {}", id);

        User user = userRepository.findById(id)
                .filter(u -> u.getRole() == UserRole.USER)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("error.user.not.found",
                            new Object[] { id }, LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });

        UserDTO dto = userMapper.toDTO(user);

        // Add statistics
        dto.setTotalBookings(user.getBookings().size());
        dto.setTotalReviews(user.getReviews().size());
        dto.setTotalComments(user.getComments().size());

        logger.debug("Found user: {}", user.getEmail());
        return dto;
    }

    /**
     * Update existing user.
     * Only allows updating users with USER role.
     *
     * @param id   the user ID
     * @param form the user form
     * @return updated user DTO
     * @throws ResourceNotFoundException  if user not found or not a USER
     * @throws DuplicateResourceException if email already exists for another user
     */
    public UserDTO updateUser(Long id, UserForm form) {
        logger.debug("Updating user ID: {}", id);

        User user = userRepository.findById(id)
                .filter(u -> u.getRole() == UserRole.USER)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("error.user.not.found",
                            new Object[] { id }, LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });

        // Check email uniqueness (if email changed)
        if (!user.getEmail().equals(form.getEmail())) {
            if (userRepository.existsByEmail(form.getEmail())) {
                String message = messageSource.getMessage("error.user.email.exists",
                        new Object[] { form.getEmail() }, LocaleContextHolder.getLocale());
                throw new DuplicateResourceException(message, "User", "email", form.getEmail());
            }
        }

        // Update user fields using MapStruct
        userMapper.updateEntityFromForm(form, user);

        // Update password if provided
        if (form.getPassword() != null && !form.getPassword().trim().isEmpty()) {
            // Password match validation is now handled by @PasswordMatches annotation
            user.setPassword(passwordEncoder.encode(form.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        logger.info("Updated user with ID: {}, Email: {}", updatedUser.getId(), updatedUser.getEmail());

        return userMapper.toDTO(updatedUser);
    }

    /**
     * Soft delete a user.
     * Only allows deleting users with USER role.
     *
     * @param id the user ID
     * @throws ResourceNotFoundException if user not found or not a USER
     * @throws IllegalStateException     if user already deleted
     */
    public void deleteUser(Long id) {
        logger.debug("Soft deleting user ID: {}", id);

        User user = userRepository.findById(id)
                .filter(u -> u.getRole() == UserRole.USER)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("error.user.not.found",
                            new Object[] { id }, LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });

        user.softDelete();
        userRepository.save(user);

        logger.info("Soft deleted user with ID: {}, Email: {}", user.getId(), user.getEmail());
    }

    /**
     * Reactivate a soft-deleted user.
     * Only allows reactivating users with USER role.
     *
     * @param id the user ID
     * @throws ResourceNotFoundException if user not found or not a USER
     * @throws IllegalStateException     if user not deleted
     */
    public void activateUser(Long id) {
        logger.debug("Reactivating user ID: {}", id);

        User user = userRepository.findById(id)
                .filter(u -> u.getRole() == UserRole.USER)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("error.user.not.found",
                            new Object[] { id }, LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });

        user.restore();
        userRepository.save(user);

        logger.info("Reactivated user with ID: {}, Email: {}", user.getId(), user.getEmail());
    }

    /**
     * Get user statistics (USER role only).
     *
     * @return statistics object
     */
    @Transactional(readOnly = true)
    public UserStatistics getStatistics() {
        long totalUsers = userRepository.countByRole(UserRole.USER);
        long activeUsers = userRepository.countByRoleAndIsActive(UserRole.USER, true);
        long inactiveUsers = userRepository.countByRoleAndIsActive(UserRole.USER, false);

        return new UserStatistics(totalUsers, activeUsers, inactiveUsers);
    }

    /**
     * Create pageable object from search criteria.
     *
     * @param criteria the search criteria
     * @return pageable object
     */
    private Pageable createPageable(UserSearchCriteria criteria) {
        Sort sort = Sort.by(Sort.Direction.fromString(criteria.getSortDir()), criteria.getSortBy());
        return PageRequest.of(criteria.getPage(), criteria.getSize(), sort);
    }

    /**
     * Inner class for user statistics.
     */
    public static class UserStatistics {
        private final long totalUsers;
        private final long activeUsers;
        private final long inactiveUsers;

        public UserStatistics(long totalUsers, long activeUsers, long inactiveUsers) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.inactiveUsers = inactiveUsers;
        }

        public long getTotalUsers() {
            return totalUsers;
        }

        public long getActiveUsers() {
            return activeUsers;
        }

        public long getInactiveUsers() {
            return inactiveUsers;
        }
    }
}
