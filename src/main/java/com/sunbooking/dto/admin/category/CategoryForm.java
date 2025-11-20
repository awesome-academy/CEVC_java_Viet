package com.sunbooking.dto.admin.category;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
public class CategoryForm {
    private Long id;

    @NotBlank(message = "{validation.category.name.required}")
    @Size(max = 255, message = "{validation.category.name.size}")
    private String name;

    @NotNull(message = "{validation.category.type.required}")
    private CategoryType type;

    public boolean isEdit() {
        return id != null;
    }

    public static CategoryForm fromEntity(Category category) {
        if (category == null)
            return null;
        return CategoryForm.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .build();
    }

    /**
     * Convert from CategoryDTO to CategoryForm.
     * Used when loading data for edit form.
     */
    public static CategoryForm fromDTO(CategoryDTO dto) {
        if (dto == null)
            return null;
        return CategoryForm.builder()
                .id(dto.getId())
                .name(dto.getName())
                .type(dto.getType())
                .build();
    }

    public Category toEntity() {
        Category category = new Category();
        category.setName(this.name != null ? this.name.trim() : null);
        category.setType(this.type);
        return category;
    }

    public void updateEntity(Category category) {
        if (category != null) {
            category.setName(this.name != null ? this.name.trim() : null);
            category.setType(this.type);
        }
    }
}
