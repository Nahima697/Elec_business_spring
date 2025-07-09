package com.elec_business.notification.eventlistener;

import com.elec_business.booking.model.Booking;
import com.elec_business.user.model.AppUser;



public record BookingAcceptedEvent(Booking booking, AppUser currentUser) {
}
