package com.elec_business.business;

import com.elec_business.entity.User;
import com.elec_business.entity.Booking;
import com.elec_business.entity.ChargingStation;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;


public interface BookingBusiness {
    Booking createBooking(String stationId, LocalDateTime startDate, LocalDateTime endDate, User currentUser);
    void verifyAvailability(ChargingStation station, Booking booking);
    void setBookingStatus(Booking booking);
    BigDecimal calculateTotalPrice(ChargingStation station, Booking booking);
    Booking acceptBooking(String bookingId, User currentUser);
    Booking rejectBooking(String bookingId, User currentUser);
    Booking getBookingById(String id,User currentUser)throws AccessDeniedException;
    List<Booking> getAllBookings();
    Booking updateBooking(String id,Booking booking, User currentUser);
    void deleteBooking(String id,User currentUser) throws AccessDeniedException;
    List<Booking> getMyBookings(User user);
    List<Booking> getMyRentals(User user);
}
