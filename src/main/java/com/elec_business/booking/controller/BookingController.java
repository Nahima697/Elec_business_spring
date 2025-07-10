package com.elec_business.booking.controller;

import com.elec_business.booking.service.BookingService;
import com.elec_business.booking.dto.BookingRequestDto;
import com.elec_business.booking.dto.BookingResponseDto;
import com.elec_business.user.model.AppUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/bookings")
    public ResponseEntity<BookingResponseDto> addBooking(
            @Valid @RequestBody BookingRequestDto bookingRequestDto,
            @AuthenticationPrincipal AppUser currentUser) {
        BookingResponseDto createdBookingResponse = bookingService.createBooking(bookingRequestDto, currentUser);
         return ResponseEntity.status(HttpStatus.CREATED).body(createdBookingResponse);
    }

    @PostMapping("/bookings/{id}/accept")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto validateBooking(@PathVariable UUID id,
                                              @AuthenticationPrincipal AppUser currentUser)
    {
        return bookingService.acceptBooking(id, currentUser);
    }

    @PutMapping("/bookings/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public BookingResponseDto updateBooking(@PathVariable UUID id,
                                            @Valid @RequestBody BookingRequestDto bookingRequestDto,
                                            @AuthenticationPrincipal AppUser currentUser)
    {
        return  bookingService.updateBooking(id, bookingRequestDto, currentUser);

    }
    @GetMapping("/bookings")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDto> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/bookings/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto getBooking(@PathVariable UUID id) {
        return bookingService.getBookingById(id);
    }

    @DeleteMapping("/bookings/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBooking(@PathVariable UUID id) {
        bookingService.deleteBooking(id);
    }
}
