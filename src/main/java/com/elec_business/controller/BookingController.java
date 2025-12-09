package com.elec_business.controller;

import com.elec_business.business.BookingBusiness;
import com.elec_business.controller.dto.BookingRequestDto;
import com.elec_business.controller.dto.BookingResponseDto;
import com.elec_business.controller.mapper.BookingMapper;
import com.elec_business.entity.User;
import com.elec_business.entity.Booking;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class   BookingController {

    private final BookingBusiness bookingBusiness;
    private final BookingMapper bookingMapper;

    @PostMapping("/bookings")
    public ResponseEntity<BookingResponseDto> addBooking(
            @Valid @RequestBody BookingRequestDto bookingRequestDto,
            @AuthenticationPrincipal User currentUser) {
        Booking createdBooking = bookingBusiness.createBooking(bookingRequestDto.getStationId(),bookingRequestDto.getStartDate(),bookingRequestDto.getEndDate(), currentUser);
         return ResponseEntity.status(HttpStatus.CREATED).body(bookingMapper.toResponseDto(createdBooking));
    }

    @PostMapping("/bookings/{id}/accept")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto validateBooking(@PathVariable String id,
                                              @AuthenticationPrincipal User currentUser)
    {
        return bookingMapper.toResponseDto(bookingBusiness.acceptBooking(id, currentUser));
    }

    @PostMapping("/bookings/{id}/reject")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto rejectBooking(@PathVariable String id,
                                              @AuthenticationPrincipal User currentUser)
    {
        return bookingMapper.toResponseDto(bookingBusiness.rejectBooking(id, currentUser));
    }

    @PutMapping("/bookings/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public BookingResponseDto updateBooking(@PathVariable String id,
                                            @Valid @RequestBody BookingRequestDto bookingRequestDto,
                                            @AuthenticationPrincipal User currentUser)
    {
        return  bookingMapper.toResponseDto(bookingBusiness.updateBooking(id,bookingMapper.toEntity(bookingRequestDto),currentUser));

    }
    @GetMapping("/bookings")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDto> getAllBookings() {
        return bookingMapper.toDtos(bookingBusiness.getAllBookings());
    }

    @GetMapping("/bookings/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto getBooking(@PathVariable String id) {

        return bookingMapper.toResponseDto(bookingBusiness.getBookingById(id));
    }

    @DeleteMapping("/bookings/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBooking(@PathVariable String id) {
        bookingBusiness.deleteBooking(id);
    }

    @GetMapping("/bookings/owner/me")
    public List<BookingResponseDto> getMyStationsBookings(@AuthenticationPrincipal User user) {
        List<Booking> bookings = bookingBusiness.getMyBookings(user);
        return bookingMapper.toDtos(bookings);
    }

    @GetMapping("/bookings/me")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDto> getMyBookings(@AuthenticationPrincipal User user) {
        return bookingMapper.toDtos(bookingBusiness.getMyRentals(user));
    }
}
