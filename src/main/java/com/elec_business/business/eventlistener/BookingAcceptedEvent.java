package com.elec_business.business.eventlistener;

import com.elec_business.entity.Booking;
import com.elec_business.entity.User;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BookingAcceptedEvent {
    private final Booking booking;
    private final User currentUser;

    public BookingAcceptedEvent(Booking booking,User currentUser) {
        this.booking = booking;
        this.currentUser = currentUser;
    }

}

