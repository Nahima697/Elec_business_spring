package com.elec_business.business;

import com.elec_business.entity.User;
import com.elec_business.entity.Booking;

public interface NotificationBusiness {
    void sendNotificationBookingAccepted(Booking booking, User currentUser);
    void sendNotificationBookingRejected(Booking booking, User currentUser);

}
