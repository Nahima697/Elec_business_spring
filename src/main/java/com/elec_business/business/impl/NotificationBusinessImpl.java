package com.elec_business.business.impl;

import com.elec_business.business.NotificationBusiness;
import com.elec_business.controller.dto.NotificationResponseDTO;
import com.elec_business.controller.mapper.NotificationMapper;
import com.elec_business.entity.Booking;
import com.elec_business.entity.Notification;
import com.elec_business.entity.User;
import com.elec_business.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationBusinessImpl implements NotificationBusiness {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    private static final Logger log =
            LoggerFactory.getLogger(NotificationBusinessImpl.class);

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendNotificationBookingAccepted(Booking booking) {

        Notification notif = new Notification();
        notif.setUser(booking.getUser());
        notif.setMessage("Votre réservation a été acceptée !");
        notif.setType("RESERVATION_CONFIRMED");
        notif.setIsRead(false);
        notif.setCreatedAt(OffsetDateTime.now());

        log.info("Envoi notification ACCEPTED à {}", booking.getUser().getEmail());

        notificationRepository.save(notif);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendNotificationBookingRejected(Booking booking) {

        Notification notif = new Notification();
        notif.setUser(booking.getUser());
        notif.setMessage("Votre réservation a été refusée !");
        notif.setType("RESERVATION_REJECTED");
        notif.setIsRead(false);
        notif.setCreatedAt(OffsetDateTime.now());

        log.info("Envoi notification REJECTED à {}", booking.getUser().getEmail());

        notificationRepository.save(notif);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getMyNotifications(User user) {

        return notificationMapper.toDTO(
                notificationRepository.findByUserId(user.getId())
        );
    }

    @Override
    @Transactional
    public void markAsRead(String id) {

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Notification not found"));

        notification.setIsRead(true);
        // pas besoin de save() → JPA dirty checking
    }
}
