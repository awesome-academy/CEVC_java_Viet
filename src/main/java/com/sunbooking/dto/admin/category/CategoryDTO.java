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
public class CategoryDTO {
    private Long id;
    private String name;
    private CategoryType type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public String getTypeName() {
        return type != null ? type.name() : "";
    }

    public static CategoryDTO fromEntity(Category category) {
        if (category == null)
            return null;
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .deletedAt(category.getDeletedAt())
                .build();
    }
}
