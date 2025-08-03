package com.elec_business.business;

import com.elec_business.entity.User;
import com.elec_business.entity.Booking;
import com.elec_business.entity.ChargingStation;

import java.math.BigDecimal;
import java.util.List;


public interface BookingBusiness {
    Booking createBooking(Booking booking, User currentUser);
    void createAndSaveTimeSlot(ChargingStation station, Booking booking);
    void verifyAvailability(ChargingStation station, Booking booking);
    void setBookingStatus(Booking booking);
    BigDecimal calculateTotalPrice(ChargingStation station, Booking booking);
    Booking acceptBooking(String bookingId, User currentUser);
    Booking getBookingById(String id);
    List<Booking> getAllBookings();
    Booking updateBooking(String id,Booking booking, User currentUser);
    void deleteBooking(String id);
}
