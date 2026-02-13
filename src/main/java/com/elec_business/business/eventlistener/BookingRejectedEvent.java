package com.elec_business.business.eventlistener;

import com.elec_business.entity.Booking;
import com.elec_business.entity.User;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BookingRejectedEvent {
    private final Booking booking;

    public BookingRejectedEvent(Booking booking) {
        this.booking = booking;

    }
}
