package com.elec_business.business.impl;

import com.elec_business.entity.Booking;
import com.elec_business.entity.Notification;
import com.elec_business.entity.User;
import com.elec_business.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationBusinessTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationBusinessImpl notificationBusiness;

    @Test
    void sendNotificationBookingAccepted_Success() {
        // ARRANGE
        User user = new User();
        user.setEmail("test@test.com");
        Booking booking = new Booking();
        booking.setUser(user);

        // ACT
        notificationBusiness.sendNotificationBookingAccepted(booking, null);

        // ASSERT
        verify(notificationRepository).save(any(Notification.class));
        verify(notificationRepository).flush();
    }

    @Test
    void sendNotificationBookingRejected_Success() {
        User user = new User();
        user.setEmail("test@test.com");
        Booking booking = new Booking();
        booking.setUser(user);

        notificationBusiness.sendNotificationBookingRejected(booking, null);

        verify(notificationRepository).save(any(Notification.class));
        verify(notificationRepository).flush();
    }
}