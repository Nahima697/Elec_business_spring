package com.elec_business.business.eventlistener;

import com.elec_business.entity.Booking;
import com.elec_business.entity.User;
import lombok.Getter;
import lombok.Setter;


@Getter
public class BookingAcceptedEvent {

    private final Booking booking;

    public BookingAcceptedEvent(Booking booking) {
        this.booking = booking;
    }
}

