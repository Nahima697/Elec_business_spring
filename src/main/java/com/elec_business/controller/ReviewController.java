package com.elec_business.controller;

import com.elec_business.business.ReviewBusiness;
import com.elec_business.controller.dto.AverageRatingDTO;
import com.elec_business.controller.dto.CreateReviewDTO;
import com.elec_business.controller.dto.ReviewResponseDTO;
import com.elec_business.controller.mapper.ReviewMapper;
import com.elec_business.entity.Review;
import com.elec_business.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewBusiness reviewBusiness;
    private final ReviewMapper reviewMapper;

    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createReview(
            @Valid @RequestBody CreateReviewDTO dto,
            @AuthenticationPrincipal User user
    ) {

        Review review = reviewBusiness.createReview(
                dto.reviewTitle(),
                dto.reviewContent(),
                dto.reviewRating(),
                user.getId(),
                dto.stationId()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewMapper.toDto(review));
    }

    @GetMapping("/station/{stationId}")
    public ResponseEntity<Page<ReviewResponseDTO>> getReviews(
            @PathVariable String stationId,
            org.springframework.data.domain.Pageable pageable
    ) {
        Page<Review> reviews = reviewBusiness.getReviews(stationId, pageable);
        return ResponseEntity.ok(reviews.map(reviewMapper::toDto));
    }

    @GetMapping("/station/{stationId}/average")
    public ResponseEntity<AverageRatingDTO> getAverageRating(
            @PathVariable String stationId
    ) {
        return ResponseEntity.ok(
                new AverageRatingDTO(
                        reviewBusiness.getAverageRating(stationId),
                        reviewBusiness.getReviewCount(stationId)
                )
        );
    }
}
