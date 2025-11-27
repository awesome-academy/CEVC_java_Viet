package com.sunbooking.dto.admin.dashboard;

import java.time.LocalDateTime;

import com.sunbooking.entity.CategoryType;
import com.sunbooking.entity.Review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for displaying recent reviews on the dashboard.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentReviewDTO {

    private Long id;
    private String userName;
    private String userEmail;
    private String title;
    private String categoryName;
    private CategoryType categoryType;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private long commentCount;
    private long likeCount;

    /**
     * Create DTO from entity.
     *
     * @param review the review entity
     * @return DTO instance
     */
    public static RecentReviewDTO fromEntity(Review review) {
        return RecentReviewDTO.builder()
                .id(review.getId())
                .userName(review.getUser() != null ? review.getUser().getName() : "N/A")
                .userEmail(review.getUser() != null ? review.getUser().getEmail() : "N/A")
                .title(review.getTitle())
                .categoryName(review.getCategory() != null ? review.getCategory().getName() : "N/A")
                .categoryType(review.getCategory() != null ? review.getCategory().getType() : null)
                .isActive(review.getIsActive())
                .createdAt(review.getCreatedAt())
                .commentCount(review.getComments() != null ? review.getComments().size() : 0)
                .likeCount(review.getLikes() != null ? review.getLikes().size() : 0)
                .build();
    }
}
