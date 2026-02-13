package com.elec_business.business;

import com.elec_business.controller.dto.NotificationResponseDTO;
import com.elec_business.entity.Booking;
import com.elec_business.entity.User;

import java.util.List;

public interface NotificationBusiness {

    void sendNotificationBookingAccepted(Booking booking);

    void sendNotificationBookingRejected(Booking booking);

    List<NotificationResponseDTO> getMyNotifications(User user);

    void markAsRead(String id);
}
