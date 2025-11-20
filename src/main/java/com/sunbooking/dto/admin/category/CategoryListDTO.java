package com.sunbooking.dto.admin.category;

import java.time.LocalDateTime;

import com.sunbooking.entity.Category;
import com.sunbooking.entity.CategoryType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryListDTO {
    private Long id;
    private String name;
    private CategoryType type;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public static CategoryListDTO fromEntity(Category category) {
        if (category == null)
            return null;
        return CategoryListDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .createdAt(category.getCreatedAt())
                .deletedAt(category.getDeletedAt())
                .build();
    }
}
