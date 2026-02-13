package com.elec_business.business.impl;

import com.elec_business.business.exception.BusinessException;
import com.elec_business.controller.dto.ReviewResponseDTO;
import com.elec_business.controller.mapper.ReviewMapper;
import com.elec_business.entity.*;
import com.elec_business.repository.BookingRepository;
import com.elec_business.repository.ChargingStationRepository;
import com.elec_business.repository.ReviewRepository;
import com.elec_business.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewBusinessTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ChargingStationRepository stationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewBusinessImpl reviewBusiness;

    @Test
    void createReview_Success() {

        String userId = "user-1";
        String stationId = "station-1";

        User user = new User(); user.setId(userId);
        ChargingStation station = new ChargingStation(); station.setId(stationId);

        Review review = new Review();
        review.setTitle("Titre");
        review.setRating(5);

        ReviewResponseDTO dto = mock(ReviewResponseDTO.class);

        when(bookingRepository.existsAcceptedBooking(
                eq(userId), eq(stationId), eq(BookingStatusType.ACCEPTED)))
                .thenReturn(true);

        when(reviewRepository.existsByUserAndStation(userId, stationId))
                .thenReturn(false);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(stationRepository.findById(stationId))
                .thenReturn(Optional.of(station));

        when(reviewRepository.save(any(Review.class)))
                .thenReturn(review);

        when(reviewMapper.toDto(review))
                .thenReturn(dto);

        ReviewResponseDTO result =
                reviewBusiness.createReview("Titre", "Contenu", 5, userId, stationId);

        assertNotNull(result);
        verify(reviewRepository).save(any(Review.class));
        verify(reviewMapper).toDto(review);
    }

    @Test
    void createReview_Fail_NoCompletedBooking() {

        String userId = "user-1";
        String stationId = "station-1";

        when(bookingRepository.existsAcceptedBooking(
                eq(userId), eq(stationId), eq(BookingStatusType.ACCEPTED)))
                .thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                reviewBusiness.createReview("Titre", "Contenu", 5, userId, stationId)
        );

        assertEquals(
                "You must have a completed booking to review this station.",
                ex.getMessage()
        );

        verify(reviewRepository, never()).save(any());
    }

    @Test
    void createReview_Fail_AlreadyReviewed() {

        String userId = "user-1";
        String stationId = "station-1";

        when(bookingRepository.existsAcceptedBooking(
                eq(userId), eq(stationId), eq(BookingStatusType.ACCEPTED)))
                .thenReturn(true);

        when(reviewRepository.existsByUserAndStation(userId, stationId))
                .thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                reviewBusiness.createReview("Titre", "Contenu", 5, userId, stationId)
        );

        assertEquals("You already reviewed this station.", ex.getMessage());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void getReviews_Success() {

        String stationId = "station-1";
        Pageable pageable = PageRequest.of(0, 10);

        Review review = new Review();
        Page<Review> page = new PageImpl<>(Collections.singletonList(review));

        ReviewResponseDTO dto = mock(ReviewResponseDTO.class);

        when(reviewRepository.findByStationIdOrderByCreatedAtDesc(
                stationId, pageable))
                .thenReturn(page);

        when(reviewMapper.toDto(review))
                .thenReturn(dto);

        Page<ReviewResponseDTO> result =
                reviewBusiness.getReviews(stationId, pageable);

        assertNotNull(result);
        verify(reviewRepository)
                .findByStationIdOrderByCreatedAtDesc(stationId, pageable);
    }

    @Test
    void getAverageRating_Success() {

        String stationId = "station-1";

        when(reviewRepository.findAverageRatingByStationId(stationId))
                .thenReturn(Optional.of(4.5));

        Double rating = reviewBusiness.getAverageRating(stationId);

        assertEquals(4.5, rating);
    }

    @Test
    void getAverageRating_DefaultZero() {

        String stationId = "station-1";

        when(reviewRepository.findAverageRatingByStationId(stationId))
                .thenReturn(Optional.empty());

        Double rating = reviewBusiness.getAverageRating(stationId);

        assertEquals(0.0, rating);
    }

    @Test
    void getReviewCount_Success() {

        String stationId = "station-1";

        when(reviewRepository.countByStationId(stationId))
                .thenReturn(10L);

        Long count = reviewBusiness.getReviewCount(stationId);

        assertEquals(10L, count);
    }
}
