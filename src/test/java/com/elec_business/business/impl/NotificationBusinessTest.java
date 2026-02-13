package com.elec_business.business.impl;

import com.elec_business.controller.dto.NotificationResponseDTO;
import com.elec_business.controller.mapper.NotificationMapper;
import com.elec_business.entity.Booking;
import com.elec_business.entity.Notification;
import com.elec_business.entity.User;
import com.elec_business.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationBusinessTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationBusinessImpl notificationBusiness;

    @Test
    void sendNotificationBookingAccepted_Success() {

        User user = new User();
        user.setEmail("test@test.com");

        Booking booking = new Booking();
        booking.setUser(user);

        notificationBusiness.sendNotificationBookingAccepted(booking);

        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);

        verify(notificationRepository).save(captor.capture());

        Notification saved = captor.getValue();

        assertEquals(user, saved.getUser());
        assertEquals("Votre réservation a été acceptée !", saved.getMessage());
        assertEquals("RESERVATION_CONFIRMED", saved.getType());
        assertFalse(saved.getIsRead());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void sendNotificationBookingRejected_Success() {

        User user = new User();
        user.setEmail("test@test.com");

        Booking booking = new Booking();
        booking.setUser(user);

        notificationBusiness.sendNotificationBookingRejected(booking);

        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);

        verify(notificationRepository).save(captor.capture());

        Notification saved = captor.getValue();

        assertEquals(user, saved.getUser());
        assertEquals("Votre réservation a été refusée !", saved.getMessage());
        assertEquals("RESERVATION_REJECTED", saved.getType());
        assertFalse(saved.getIsRead());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void getMyNotifications_Success() {

        User user = new User();
        user.setId("u1");

        List<Notification> entities = List.of(new Notification());
        List<NotificationResponseDTO> dtos =
                List.of(mock(NotificationResponseDTO.class));

        when(notificationRepository.findByUserId("u1"))
                .thenReturn(entities);

        when(notificationMapper.toDTO(entities))
                .thenReturn(dtos);

        List<NotificationResponseDTO> result =
                notificationBusiness.getMyNotifications(user);

        assertNotNull(result);
        verify(notificationRepository).findByUserId("u1");
        verify(notificationMapper).toDTO(entities);
    }

    @Test
    void markAsRead_Success() {

        Notification notif = new Notification();
        notif.setIsRead(false);

        when(notificationRepository.findById("n1"))
                .thenReturn(Optional.of(notif));

        notificationBusiness.markAsRead("n1");

        assertTrue(notif.getIsRead());
        verify(notificationRepository).findById("n1");
        // Pas de save() → dirty checking
    }

    @Test
    void markAsRead_NotFound() {

        when(notificationRepository.findById("n1"))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                notificationBusiness.markAsRead("n1")
        );
    }
}
