package com.sunbooking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.sunbooking.dto.admin.admin.AdminDTO;
import com.sunbooking.dto.admin.admin.AdminForm;
import com.sunbooking.entity.User;

/**
 * MapStruct mapper for Admin entity and DTOs.
 * Handles conversions between User entity (with ADMIN role) and various admin
 * DTOs.
 * Generated implementation will be a Spring bean.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AdminMapper {

    /**
     * Convert User entity to AdminDTO.
     * Used for displaying admin details.
     *
     * @param user the admin user entity
     * @return admin DTO
     */
    AdminDTO toDTO(User user);

    /**
     * Convert AdminDTO to AdminForm.
     * Used for populating edit forms from existing admin data.
     *
     * @param dto the admin DTO
     * @return admin form
     */
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "confirmPassword", ignore = true)
    AdminForm dtoToForm(AdminDTO dto);

    /**
     * Convert User entity to AdminForm.
     * Alternative method for direct entity-to-form conversion.
     *
     * @param user the admin user entity
     * @return admin form
     */
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "confirmPassword", ignore = true)
    AdminForm toForm(User user);

    /**
     * Update existing User entity with data from AdminForm.
     * Used for editing admins. Password is handled separately for security.
     *
     * @param form the admin form with new data
     * @param user the existing admin user entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "likes", ignore = true)
    void updateEntityFromForm(AdminForm form, @MappingTarget User user);

    /**
     * Convert AdminForm to new User entity.
     * Used for creating new admins. Password must be encoded separately.
     *
     * @param form the admin form
     * @return new admin user entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", constant = "ADMIN")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "likes", ignore = true)
    User toEntity(AdminForm form);
}
