package com.elec_business.controller;

import com.elec_business.dto.BookingRequestDto;
import com.elec_business.dto.BookingResponseDto;
import com.elec_business.entity.AppUser;
import com.elec_business.entity.Booking;
import com.elec_business.mapper.BookingMapper;
import com.elec_business.mapper.BookingResponseMapper;
import com.elec_business.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;
    private final BookingResponseMapper bookingResponseMapper;

    @PostMapping("/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto addBooking(
            @Valid @RequestBody BookingRequestDto bookingRequestDto,
            @AuthenticationPrincipal AppUser currentUser) {
        Booking createdBooking = bookingService.createBooking(bookingRequestDto, currentUser);
        return bookingResponseMapper.toDto(createdBooking);
    }

    @PostMapping("/bookings/{id}/accept")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto validateBooking(@PathVariable UUID id,
                                              @AuthenticationPrincipal AppUser currentUser) throws AccessDeniedException {
        Booking booking = bookingService.acceptBooking(id, currentUser);
        return bookingResponseMapper.toDto(booking);
    }

    @PutMapping("/bookings/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public BookingResponseDto updateBooking(@PathVariable UUID id,
                                            @Valid @RequestBody BookingRequestDto bookingRequestDto,
                                            @AuthenticationPrincipal AppUser currentUser) throws AccessDeniedException {
        Booking updated = bookingService.updateBooking(id, bookingRequestDto, currentUser);
        return bookingResponseMapper.toDto(updated); // Correction ici
    }

    @GetMapping("/bookings")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDto> getAllBookings() {
        return bookingService.getAllBookings().stream()
                .map(bookingResponseMapper::toDto)
                .toList();
    }

    @GetMapping("/bookings/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto getBooking(@PathVariable UUID id) {
        return bookingResponseMapper.toDto(bookingService.getBookingById(id));
    }

    @DeleteMapping("/bookings/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBooking(@PathVariable UUID id) {
        bookingService.deleteBooking(id);
    }
}
