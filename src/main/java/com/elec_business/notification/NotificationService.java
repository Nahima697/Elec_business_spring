package com.elec_business.notification;

import com.elec_business.booking.model.Booking;
import com.elec_business.user.model.AppUser;
import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void sendNotificationBookingAccepted(Booking booking, AppUser currentUser) {
        Notification notif = new Notification();
        notif.setUser(booking.getUser());
        notif.setMessage("Votre réservation a été acceptée !");
        notificationRepository.save(notif);

    }

}
