package com.sunbooking.dto.admin.category;

import com.sunbooking.entity.CategoryType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategorySearchCriteria {
    private String keyword;
    private CategoryType type;
    private Boolean showDeleted = false;
    private int page = 0;
    private int size = 10;
    private String sortBy = "createdAt";
    private String sortDir = "DESC";

    public boolean hasKeyword() {
        return keyword != null && !keyword.trim().isEmpty();
    }

    public boolean hasTypeFilter() {
        return type != null;
    }

    public boolean isShowDeleted() {
        return showDeleted != null && showDeleted;
    }

    public String getTrimmedKeyword() {
        return hasKeyword() ? keyword.trim() : "";
    }
}
