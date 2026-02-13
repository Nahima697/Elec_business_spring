package com.elec_business.business.impl;

import com.elec_business.business.ReviewBusiness;
import com.elec_business.business.exception.BusinessException;
import com.elec_business.controller.dto.ReviewResponseDTO;
import com.elec_business.controller.mapper.ReviewMapper;
import com.elec_business.entity.BookingStatusType;
import com.elec_business.entity.Review;
import com.elec_business.repository.BookingRepository;
import com.elec_business.repository.ChargingStationRepository;
import com.elec_business.repository.ReviewRepository;
import com.elec_business.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class ReviewBusinessImpl implements ReviewBusiness {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final ChargingStationRepository stationRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    @Override
    @Transactional
    public ReviewResponseDTO createReview(
            String title,
            String content,
            Integer rating,
            String userId,
            String stationId) {

        boolean hasBooked = bookingRepository.existsAcceptedBooking(
                userId,
                stationId,
                BookingStatusType.ACCEPTED
        );

        if (!hasBooked) {
            throw new BusinessException(
                    "You must have a completed booking to review this station.");
        }

        if (reviewRepository.existsByUserAndStation(userId, stationId)) {
            throw new BusinessException(
                    "You already reviewed this station.");
        }

        Review review = new Review();
        review.setUser(userRepository.findById(userId).orElseThrow());
        review.setStation(stationRepository.findById(stationId).orElseThrow());
        review.setTitle(title);
        review.setComments(content);
        review.setRating(rating);
        review.setCreatedAt(OffsetDateTime.now());

        Review savedReview = reviewRepository.save(review);

        return reviewMapper.toDto(savedReview);
    }

    @Override
    public Double getAverageRating(String stationId) {
        return reviewRepository
                .findAverageRatingByStationId(stationId)
                .orElse(0.0);
    }

    @Override
    public Long getReviewCount(String stationId) {
        return reviewRepository.countByStationId(stationId);
    }

    @Override
    @Transactional
    public Page<ReviewResponseDTO> getReviews(String stationId, Pageable pageable) {

        return reviewRepository
                .findByStationIdOrderByCreatedAtDesc(stationId, pageable)
                .map(reviewMapper::toDto);
    }
}
