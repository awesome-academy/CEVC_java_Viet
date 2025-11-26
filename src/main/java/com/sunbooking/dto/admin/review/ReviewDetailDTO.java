package com.sunbooking.dto.admin.review;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.sunbooking.entity.CategoryType;
import com.sunbooking.entity.Comment;
import com.sunbooking.entity.Review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Detailed DTO for review information.
 * Includes user, category, tour information and comments for detail view.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDetailDTO {

    private Long id;
    private String title;
    private String content;
    private Long userId;
    private String userName;
    private String userEmail;
    private String userPhone;
    private Long categoryId;
    private String categoryName;
    private CategoryType categoryType;
    private Long tourId;
    private String tourTitle;
    private Integer likeCount;
    private Integer commentCount;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    @Builder.Default
    private List<CommentDTO> comments = new ArrayList<>();

    /**
     * Convert Review entity to ReviewDetailDTO using Builder pattern.
     * Note: Caller should validate review integrity using validator before calling
     * this method.
     *
     * @param review the review entity (must not be null, must have user and
     *               category)
     * @return the review detail DTO
     */
    public static ReviewDetailDTO fromEntity(Review review) {
        return ReviewDetailDTO.builder()
                .id(review.getId())
                .title(review.getTitle())
                .content(review.getContent())
                .userId(review.getUser().getId())
                .userName(review.getUser().getName())
                .userEmail(review.getUser().getEmail())
                .userPhone(review.getUser().getPhone())
                .categoryId(review.getCategory().getId())
                .categoryName(review.getCategory().getName())
                .categoryType(review.getCategory().getType())
                .tourId(review.getTour() != null ? review.getTour().getId() : null)
                .tourTitle(review.getTour() != null ? review.getTour().getTitle() : null)
                .likeCount(review.getLikeCount())
                .commentCount(review.getCommentCount())
                .isActive(review.getIsActive())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .deletedAt(review.getDeletedAt())
                .comments(review.getComments() != null
                        ? review.getComments().stream()
                                .map(CommentDTO::fromEntity)
                                .collect(Collectors.toList())
                        : new ArrayList<>())
                .build();
    }

    /**
     * Nested DTO for comment information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentDTO {
        private Long id;
        private String content;
        private String userName;
        private String userEmail;
        private Long parentCommentId;
        private Boolean isActive;
        private LocalDateTime createdAt;
        private LocalDateTime deletedAt;
        @Builder.Default
        private List<CommentDTO> replies = new ArrayList<>();

        public static CommentDTO fromEntity(Comment comment) {
            return CommentDTO.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .userName(comment.getUser().getName())
                    .userEmail(comment.getUser().getEmail())
                    .parentCommentId(comment.getParentComment() != null
                            ? comment.getParentComment().getId()
                            : null)
                    .isActive(comment.getIsActive())
                    .createdAt(comment.getCreatedAt())
                    .deletedAt(comment.getDeletedAt())
                    .replies(comment.getReplies() != null
                            ? comment.getReplies().stream()
                                    .map(CommentDTO::fromEntity)
                                    .collect(Collectors.toList())
                            : new ArrayList<>())
                    .build();
        }
    }
}
