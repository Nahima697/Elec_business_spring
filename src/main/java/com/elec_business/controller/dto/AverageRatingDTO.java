package com.elec_business.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AverageRatingDTO {
    private Double averageRating;
    private Long totalReviews;
}

