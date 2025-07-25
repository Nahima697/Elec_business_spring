package com.elec_business.business.eventlistener;

import com.elec_business.entity.Booking;
import com.elec_business.entity.AppUser;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BookingAcceptedEvent {
    private final Booking booking;
    private final AppUser currentUser;

    public BookingAcceptedEvent(Booking booking, AppUser currentUser) {
        this.booking = booking;
        this.currentUser = currentUser;
    }

}

