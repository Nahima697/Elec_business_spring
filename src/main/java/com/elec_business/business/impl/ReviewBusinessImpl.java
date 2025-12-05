package com.elec_business.business.impl;

import com.elec_business.business.ReviewBusiness;
import com.elec_business.business.exception.BusinessException;
import com.elec_business.entity.Review;
import com.elec_business.repository.BookingRepository;
import com.elec_business.repository.ChargingStationRepository;
import com.elec_business.repository.ReviewRepository;
import com.elec_business.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import  org.springframework.data.domain.Page;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor

public class ReviewBusinessImpl implements ReviewBusiness {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final ChargingStationRepository stationRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public Review createReview(String title, String content, Integer rating, String userId, String stationId) {

        boolean hasBooked = bookingRepository.existsByUserAndStationAndStatusAccepted(userId, stationId);

        if (!hasBooked) {
            throw new BusinessException("You must have a completed booking to review this station.");
        }

        if (reviewRepository.existsByUserAndStation(userId, stationId)) {
            throw new BusinessException("You already reviewed this station.");
        }

        Review review = new Review();
        review.setUser(userRepository.findById(userId).orElseThrow());
        review.setStation(stationRepository.findById(stationId).orElseThrow());
        review.setTitle(title);
        review.setComments(content);
        review.setRating(rating);
        review.setCreatedAt(OffsetDateTime.now());

        return reviewRepository.save(review);
    }

    @Override
    public Double getAverageRating(String stationId) {
        return reviewRepository.findAverageRatingByStationId(stationId).orElse(0.0);
    }

    @Override
    public Long getReviewCount(String stationId) {
        return reviewRepository.countByStationId(stationId);
    }

    @Override
    public Page<Review> getReviews(String stationId, org.springframework.data.domain.Pageable pageable) {
        return reviewRepository.findByStationIdOrderByCreatedAtDesc(stationId, pageable);
    }

}
