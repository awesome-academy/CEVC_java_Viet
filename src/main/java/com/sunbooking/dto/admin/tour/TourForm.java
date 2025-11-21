package com.sunbooking.dto.admin.tour;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.sunbooking.entity.Tour;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Form object for creating and editing tours.
 * Contains validation rules for tour input.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourForm {

    @NotBlank(message = "{validation.tour.title.required}")
    @Size(min = 2, max = 255, message = "{validation.tour.title.size}")
    private String title;

    @NotBlank(message = "{validation.tour.description.required}")
    @Size(min = 10, message = "{validation.tour.description.minsize}")
    private String description;

    @NotNull(message = "{validation.tour.price.required}")
    @DecimalMin(value = "0.0", inclusive = true, message = "{validation.tour.price.min}")
    @Digits(integer = 8, fraction = 2, message = "{validation.tour.price.digits}")
    private BigDecimal price;

    @Min(value = 1, message = "{validation.tour.duration.min}")
    private Integer duration; // in days

    @Min(value = 1, message = "{validation.tour.maxparticipants.min}")
    private Integer maxParticipants;

    @Size(max = 255, message = "{validation.tour.location.size}")
    private String location;

    /**
     * Convert form to Tour entity for creation.
     *
     * @return the tour entity
     */
    public Tour toEntity() {
        Tour tour = new Tour();
        tour.setTitle(this.title != null ? this.title.trim() : null);
        tour.setDescription(this.description != null ? this.description.trim() : null);
        tour.setPrice(this.price);
        tour.setDuration(this.duration);
        tour.setMaxParticipants(this.maxParticipants);
        tour.setLocation(this.location != null ? this.location.trim() : null);
        tour.setIsActive(true);
        return tour;
    }

    /**
     * Update existing tour entity with form data.
     *
     * @param tour the tour entity to update
     */
    public void updateEntity(Tour tour) {
        if (tour != null) {
            tour.setTitle(this.title != null ? this.title.trim() : null);
            tour.setDescription(this.description != null ? this.description.trim() : null);
            tour.setPrice(this.price);
            tour.setDuration(this.duration);
            tour.setMaxParticipants(this.maxParticipants);
            tour.setLocation(this.location != null ? this.location.trim() : null);
        }
    }

    /**
     * Create form from TourDTO for editing.
     *
     * @param dto the tour DTO
     * @return the tour form
     */
    public static TourForm fromDTO(TourDTO dto) {
        if (dto == null) {
            return null;
        }

        return TourForm.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .duration(dto.getDuration())
                .maxParticipants(dto.getMaxParticipants())
                .location(dto.getLocation())
                .build();
    }
}
