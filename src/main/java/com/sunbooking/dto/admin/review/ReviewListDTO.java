package com.sunbooking.dto.admin.review;

import java.time.LocalDateTime;

import com.sunbooking.entity.CategoryType;
import com.sunbooking.entity.Review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lightweight DTO for displaying reviews in list view.
 * Contains essential information for listing reviews in admin panel.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewListDTO {

    private Long id;
    private String title;
    private String userName;
    private String userEmail;
    private String categoryName;
    private CategoryType categoryType;
    private String tourTitle;
    private Integer likeCount;
    private Integer commentCount;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    /**
     * Convert Review entity to ReviewListDTO using Builder pattern.
     * Note: Caller should validate review integrity using validator before calling
     * this method.
     *
     * @param review the review entity (must not be null, must have user and
     *               category)
     * @return the review list DTO
     */
    public static ReviewListDTO fromEntity(Review review) {
        return ReviewListDTO.builder()
                .id(review.getId())
                .title(review.getTitle())
                .userName(review.getUser().getName())
                .userEmail(review.getUser().getEmail())
                .categoryName(review.getCategory().getName())
                .categoryType(review.getCategory().getType())
                .tourTitle(review.getTour() != null ? review.getTour().getTitle() : null)
                .likeCount(review.getLikeCount())
                .commentCount(review.getCommentCount())
                .isActive(review.getIsActive())
                .createdAt(review.getCreatedAt())
                .deletedAt(review.getDeletedAt())
                .build();
    }
}
