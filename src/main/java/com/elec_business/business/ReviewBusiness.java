package com.elec_business.business;

import com.elec_business.controller.dto.ReviewResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewBusiness {

    ReviewResponseDTO createReview(
            String reviewTitle,
            String reviewContent,
            Integer reviewRating,
            String userId,
            String stationId
    );

    Double getAverageRating(String stationId);

    Long getReviewCount(String stationId);

    Page<ReviewResponseDTO> getReviews(String stationId, Pageable pageable);
}
