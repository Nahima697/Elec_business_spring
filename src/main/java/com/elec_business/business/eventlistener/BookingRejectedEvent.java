package com.elec_business.business.eventlistener;

import com.elec_business.entity.Booking;
import com.elec_business.entity.User;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BookingRejectedEvent {
    private final Booking booking;
    private final User currentUser;

    public BookingRejectedEvent(Booking booking,User currentUser) {
        this.booking = booking;
        this.currentUser = currentUser;
    }
}
