package com.elec_business.controller;

import com.elec_business.business.ReviewBusiness;
import com.elec_business.controller.dto.AverageRatingDTO;
import com.elec_business.controller.dto.CreateReviewDTO;
import com.elec_business.controller.dto.ReviewResponseDTO;
import com.elec_business.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewBusiness reviewBusiness;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponseDTO createReview(
            @Valid @RequestBody CreateReviewDTO dto,
            @AuthenticationPrincipal User user
    ) {

        return reviewBusiness.createReview(
                dto.reviewTitle(),
                dto.reviewContent(),
                dto.reviewRating(),
                user.getId(),
                dto.stationId()
        );
    }

    @GetMapping("/station/{stationId}")
    public Page<ReviewResponseDTO> getReviews(
            @PathVariable String stationId,
            org.springframework.data.domain.Pageable pageable
    ) {
        return reviewBusiness.getReviews(stationId, pageable);
    }

    @GetMapping("/station/{stationId}/average")
    public AverageRatingDTO getAverageRating(
            @PathVariable String stationId
    ) {
        return new AverageRatingDTO(
                reviewBusiness.getAverageRating(stationId),
                reviewBusiness.getReviewCount(stationId)
        );
    }
}
