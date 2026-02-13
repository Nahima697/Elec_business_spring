package com.elec_business.business;

import com.elec_business.controller.dto.BookingResponseDto;
import com.elec_business.entity.User;
import com.elec_business.entity.Booking;
import com.elec_business.entity.ChargingStation;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;


public interface BookingBusiness {
    BookingResponseDto createBooking(String stationId, LocalDateTime startDate, LocalDateTime endDate, User currentUser);
    void verifyAvailability(ChargingStation station, Booking booking);
    void setBookingStatus(Booking booking);
    BigDecimal calculateTotalPrice(ChargingStation station, Booking booking);
    BookingResponseDto acceptBooking(String bookingId, User currentUser);
    BookingResponseDto rejectBooking(String bookingId, User currentUser);
    BookingResponseDto getBookingById(String id,User currentUser)throws AccessDeniedException;
    List<BookingResponseDto> getAllBookings();
    BookingResponseDto updateBooking(String id,Booking booking, User currentUser);
    void deleteBooking(String id,User currentUser) throws AccessDeniedException;
    List<BookingResponseDto> getMyBookings(User user);
    List<BookingResponseDto> getMyRentals(User user);
}
