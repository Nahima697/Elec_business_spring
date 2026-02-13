package com.elec_business.business.eventlistener;

import com.elec_business.business.NotificationBusiness;
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

    private final NotificationBusiness notificationService;
    private static final Logger log =
            LoggerFactory.getLogger(BookingNotificationListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookingAccepted(BookingAcceptedEvent event) {

        log.info("⚡ Notification ACCEPTED for user {}",
                event.getBooking().getUser().getEmail());

        notificationService.sendNotificationBookingAccepted(
                event.getBooking()
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookingRejected(BookingRejectedEvent event) {

        log.info("⚡ Notification REJECTED for user {}",
                event.getBooking().getUser().getEmail());

        notificationService.sendNotificationBookingRejected(
                event.getBooking()
        );
    }
}
