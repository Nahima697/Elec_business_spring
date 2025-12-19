package com.elec_business.business.impl;

import com.elec_business.business.exception.BusinessException;
import com.elec_business.entity.BookingStatusType;
import com.elec_business.entity.ChargingStation;
import com.elec_business.entity.Review;
import com.elec_business.entity.User;
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

    @InjectMocks
    private ReviewBusinessImpl reviewBusiness;

    // --- CREATE REVIEW ---

    @Test
    void createReview_Success() {
        // ARRANGE
        String userId = "user-1";
        String stationId = "station-1";

        User user = new User(); user.setId(userId);
        ChargingStation station = new ChargingStation(); station.setId(stationId);

        // 1. Simuler qu'il a bien une réservation ACCEPTED
        when(bookingRepository.existsByStation_Location_User_IdAndStation_IdAndStatus_Name(
                userId, stationId, BookingStatusType.ACCEPTED
        )).thenReturn(true);

        // 2. Simuler qu'il n'a PAS encore noté cette station
        when(reviewRepository.existsByUserAndStation(userId, stationId)).thenReturn(false);

        // 3. Simuler la récupération des entités
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));

        // 4. Simuler la sauvegarde
        when(reviewRepository.save(any(Review.class))).thenAnswer(i -> i.getArguments()[0]);

        // ACT
        Review result = reviewBusiness.createReview("Super", "Top borne", 5, userId, stationId);

        // ASSERT
        assertNotNull(result);
        assertEquals("Super", result.getTitle());
        assertEquals(5, result.getRating());
        assertNotNull(result.getCreatedAt());

        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void createReview_Fail_NoCompletedBooking() {
        String userId = "u1";
        String stationId = "s1";

        // Simuler qu'aucune réservation terminée n'existe
        when(bookingRepository.existsByStation_Location_User_IdAndStation_IdAndStatus_Name(any(), any(), any()))
                .thenReturn(false);

        // ACT & ASSERT
        BusinessException ex = assertThrows(BusinessException.class, () ->
                reviewBusiness.createReview("Titre", "Contenu", 4, userId, stationId)
        );

        assertEquals("You must have a completed booking to review this station.", ex.getMessage());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void createReview_Fail_AlreadyReviewed() {
        String userId = "u1";
        String stationId = "s1";

        // Il a réservé...
        when(bookingRepository.existsByStation_Location_User_IdAndStation_IdAndStatus_Name(any(), any(), any()))
                .thenReturn(true);
        // ... MAIS il a déjà laissé un avis
        when(reviewRepository.existsByUserAndStation(userId, stationId)).thenReturn(true);

        // ACT & ASSERT
        BusinessException ex = assertThrows(BusinessException.class, () ->
                reviewBusiness.createReview("Titre", "Contenu", 4, userId, stationId)
        );

        assertEquals("You already reviewed this station.", ex.getMessage());
        verify(reviewRepository, never()).save(any());
    }

    // --- GET DATA ---

    @Test
    void getAverageRating_Success() {
        when(reviewRepository.findAverageRatingByStationId("s1")).thenReturn(Optional.of(4.5));

        Double avg = reviewBusiness.getAverageRating("s1");

        assertEquals(4.5, avg);
    }

    @Test
    void getAverageRating_Empty() {
        when(reviewRepository.findAverageRatingByStationId("s1")).thenReturn(Optional.empty());

        Double avg = reviewBusiness.getAverageRating("s1");

        assertEquals(0.0, avg);
    }

    @Test
    void getReviewCount_Success() {
        when(reviewRepository.countByStationId("s1")).thenReturn(10L);

        Long count = reviewBusiness.getReviewCount("s1");

        assertEquals(10L, count);
    }

    @Test
    void getReviews_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> emptyPage = new PageImpl<>(Collections.emptyList());

        when(reviewRepository.findByStationIdOrderByCreatedAtDesc("s1", pageable)).thenReturn(emptyPage);

        Page<Review> result = reviewBusiness.getReviews("s1", pageable);

        assertNotNull(result);
        verify(reviewRepository).findByStationIdOrderByCreatedAtDesc("s1", pageable);
    }
}