package com.elec_business.notification.eventlistener;

import com.elec_business.booking.service.BookingService;
import com.elec_business.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BookingNotificationListener {

    private final NotificationService notificationService;
    private static Logger log = LoggerFactory.getLogger(BookingNotificationListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookingAccepted(BookingAcceptedEvent event) {
        log.info("⚡ NotificationService.sendNotificationBookingAccepted triggered for user {}", event.currentUser().getEmail());
        notificationService.sendNotificationBookingAccepted(event.booking(), event.currentUser());
    }
}
