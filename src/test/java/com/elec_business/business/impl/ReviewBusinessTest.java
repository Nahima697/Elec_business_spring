package com.elec_business.business.impl;

import com.elec_business.business.exception.BusinessException;
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

    @InjectMocks
    private ReviewBusinessImpl reviewBusiness;

    @Test
    void createReview_Success() {
        // ARRANGE
        String userId = "user-1";
        String stationId = "station-1";
        User user = new User(); user.setId(userId);
        ChargingStation station = new ChargingStation(); station.setId(stationId);

        // 1. On dit que la réservation existe bien (ACCEPTED)
        when(bookingRepository.existsAcceptedBooking(eq(userId), eq(stationId), eq(BookingStatusType.ACCEPTED)))
                .thenReturn(true);

        // 2. On dit que l'utilisateur n'a PAS encore noté
        when(reviewRepository.existsByUserAndStation(userId, stationId)).thenReturn(false);

        // 3. Mock des repositories User et Station
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));

        // 4. Mock du save (retourne l'objet passé en argument)
        when(reviewRepository.save(any(Review.class))).thenAnswer(i -> i.getArguments()[0]);

        // ACT
        Review result = reviewBusiness.createReview("Titre", "Contenu", 5, userId, stationId);

        // ASSERT
        assertNotNull(result);
        assertEquals("Titre", result.getTitle());
        assertEquals(5, result.getRating());
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void createReview_Fail_NoCompletedBooking() {
        // ARRANGE
        String userId = "user-1";
        String stationId = "station-1";

        // Le repository répond FALSE : aucune réservation acceptée
        when(bookingRepository.existsAcceptedBooking(eq(userId), eq(stationId), eq(BookingStatusType.ACCEPTED)))
                .thenReturn(false);

        // ACT & ASSERT
        BusinessException ex = assertThrows(BusinessException.class, () ->
                reviewBusiness.createReview("Titre", "Contenu", 5, userId, stationId)
        );

        assertEquals("You must have a completed booking to review this station.", ex.getMessage());

        // On vérifie qu'on n'a JAMAIS essayé de sauver
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void createReview_Fail_AlreadyReviewed() {
        // ARRANGE
        String userId = "user-1";
        String stationId = "station-1";

        // 1. Il faut que la réservation existe
        when(bookingRepository.existsAcceptedBooking(eq(userId), eq(stationId), eq(BookingStatusType.ACCEPTED)))
                .thenReturn(true);

        // 2. MAIS l'utilisateur a déjà noté
        when(reviewRepository.existsByUserAndStation(userId, stationId)).thenReturn(true);

        // ACT & ASSERT
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
        Page<Review> page = new PageImpl<>(Collections.emptyList());

        when(reviewRepository.findByStationIdOrderByCreatedAtDesc(stationId, pageable)).thenReturn(page);

        Page<Review> result = reviewBusiness.getReviews(stationId, pageable);

        assertNotNull(result);
        verify(reviewRepository).findByStationIdOrderByCreatedAtDesc(stationId, pageable);
    }

    @Test
    void getAverageRating_Success() {
        String stationId = "station-1";
        when(reviewRepository.findAverageRatingByStationId(stationId)).thenReturn(Optional.of(4.5));

        Double rating = reviewBusiness.getAverageRating(stationId);

        assertEquals(4.5, rating);
    }

    @Test
    void getReviewCount_Success() {
        String stationId = "station-1";
        when(reviewRepository.countByStationId(stationId)).thenReturn(10L);

        Long count = reviewBusiness.getReviewCount(stationId);

        assertEquals(10L, count);
    }
}