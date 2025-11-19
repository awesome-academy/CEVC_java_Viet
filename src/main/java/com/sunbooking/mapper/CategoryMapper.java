package com.sunbooking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.sunbooking.dto.admin.category.CategoryDTO;
import com.sunbooking.dto.admin.category.CategoryForm;
import com.sunbooking.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDTO toDTO(Category category);

    CategoryForm dtoToForm(CategoryDTO dto);

    CategoryForm toForm(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    void updateEntityFromForm(CategoryForm form, @MappingTarget Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    Category toEntity(CategoryForm form);
}
