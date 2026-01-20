package com.elec_business.business.impl;

import com.elec_business.business.NotificationBusiness;
import com.elec_business.entity.Booking;
import com.elec_business.entity.Notification;
import com.elec_business.repository.NotificationRepository;
import com.elec_business.entity.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.time.OffsetDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class NotificationBusinessImpl implements NotificationBusiness {

    private final NotificationRepository notificationRepository;
    private static  final Logger log = LoggerFactory.getLogger(NotificationBusinessImpl.class);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendNotificationBookingAccepted(Booking booking, User currentUser) {
        Notification notif = new Notification();
        notif.setUser(booking.getUser());
        notif.setMessage("Votre réservation a été acceptée !");
        notif.setType("RESERVATION_CONFIRMED");
        notif.setIsRead(false);
        notif.setCreatedAt(OffsetDateTime.now());

        log.info(" Envoi de notification à {}", booking.getUser().getEmail());
        notificationRepository.save(notif);
        notificationRepository.flush();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendNotificationBookingRejected(Booking booking, User currentUser) {
        Notification notif = new Notification();
        notif.setUser(booking.getUser());
        notif.setMessage("Votre réservation a été refusée !");
        notif.setType("RESERVATION_REJECTED");
        notif.setIsRead(false);
        notif.setCreatedAt(OffsetDateTime.now());

        log.info(" Envoi de notification à {}", booking.getUser().getEmail());
        notificationRepository.save(notif);
        notificationRepository.flush();
    }

    @Override
    public List<Notification> getMyNotifications(User user) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    @Override
    public void markAsRead(String id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
        });
    }

}
