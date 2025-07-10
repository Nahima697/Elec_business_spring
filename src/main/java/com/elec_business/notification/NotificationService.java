package com.elec_business.notification;

import com.elec_business.booking.model.Booking;
import com.elec_business.user.model.AppUser;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.time.OffsetDateTime;


@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private static Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendNotificationBookingAccepted(Booking booking, AppUser currentUser) {
        Notification notif = new Notification();
        notif.setUser(booking.getUser());
        notif.setMessage("Votre réservation a été acceptée !");
        notif.setType("RESERVATION_CONFIRMED");
        notif.setIsRead(false);
        notif.setCreatedAt(OffsetDateTime.now());

        log.info("✅ Envoi de notification à {}", booking.getUser().getEmail());
        notificationRepository.save(notif);
        notificationRepository.flush();
    }

}
