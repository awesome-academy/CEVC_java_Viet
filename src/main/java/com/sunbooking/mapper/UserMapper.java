package com.sunbooking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.sunbooking.dto.admin.user.UserDTO;
import com.sunbooking.dto.admin.user.UserForm;
import com.sunbooking.entity.User;

/**
 * MapStruct mapper for User entity conversions.
 * Handles mapping between User entity, DTOs, and forms.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    /**
     * Convert User entity to UserDTO.
     * Statistics fields (totalBookings, totalReviews, totalComments) are set in
     * service layer.
     *
     * @param user the user entity
     * @return the user DTO
     */
    @Mapping(target = "totalBookings", ignore = true)
    @Mapping(target = "totalReviews", ignore = true)
    @Mapping(target = "totalComments", ignore = true)
    UserDTO toDTO(User user);

    /**
     * Convert UserDTO to UserForm (for edit form).
     * Password field is intentionally not mapped for security.
     *
     * @param dto the user DTO
     * @return the user form
     */
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "confirmPassword", ignore = true)
    UserForm dtoToForm(UserDTO dto);

    /**
     * Convert User entity to UserForm (for edit form).
     * Password field is intentionally not mapped for security.
     *
     * @param user the user entity
     * @return the user form
     */
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "confirmPassword", ignore = true)
    UserForm toForm(User user);

    /**
     * Update User entity from UserForm.
     * Password is handled separately in the service layer.
     *
     * @param form the user form
     * @param user the user entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "likes", ignore = true)
    void updateEntityFromForm(UserForm form, @MappingTarget User user);

    /**
     * Convert UserForm to new User entity (for create).
     * Password will be encoded in the service layer.
     *
     * @param form the user form
     * @return new user entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "likes", ignore = true)
    User toEntity(UserForm form);
}
