package com.elec_business.business;

import com.elec_business.entity.Notification;
import com.elec_business.entity.User;
import com.elec_business.entity.Booking;

import java.util.List;

public interface NotificationBusiness {
    void sendNotificationBookingAccepted(Booking booking, User currentUser);
    void sendNotificationBookingRejected(Booking booking, User currentUser);
    List<Notification> getMyNotifications(User user);
    void markAsRead(String id);

}
