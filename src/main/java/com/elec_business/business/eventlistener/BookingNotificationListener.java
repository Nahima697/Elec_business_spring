package com.elec_business.business.eventlistener;

import com.elec_business.business.impl.NotificationBusinessImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BookingNotificationListener {

    private final NotificationBusinessImpl notificationService;
    private static Logger log = LoggerFactory.getLogger(BookingNotificationListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookingAccepted(BookingAcceptedEvent event) {
        log.info("⚡ NotificationService.sendNotificationBookingAccepted triggered for user {}", event.getCurrentUser().getEmail());
        notificationService.sendNotificationBookingAccepted(event.getBooking(), event.getCurrentUser());
    }
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookingRejected(BookingRejectedEvent event) {
        log.info("⚡ NotificationService.sendNotificationBookingRejected triggered for user {}", event.getCurrentUser().getEmail());
        notificationService.sendNotificationBookingRejected(event.getBooking(), event.getCurrentUser());
    }

}
