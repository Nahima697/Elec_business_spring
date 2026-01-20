package com.elec_business.business.impl;

import com.elec_business.business.TimeSlotBusiness;
import com.elec_business.entity.*;
import com.elec_business.repository.BookingRepository;
import com.elec_business.repository.BookingStatusRepository;
import com.elec_business.repository.ChargingStationRepository;
import com.elec_business.repository.TimeSlotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingBusinessTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ChargingStationRepository chargingStationRepository;
    @Mock
    private TimeSlotRepository timeSlotRepository;
    @Mock
    private BookingStatusRepository bookingStatusRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private TimeSlotBusiness timeSlotBusiness;

    @InjectMocks
    private BookingBusinessImpl bookingBusiness;

    // --- TESTS CREATE BOOKING ---
    @Test
    void createBooking_Success() {
        User renter = new User(); renter.setId("renter-1");
        User owner = new User(); owner.setId("owner-1");
        ChargingLocation location = new ChargingLocation(); location.setUser(owner);
        ChargingStation station = new ChargingStation();
        station.setId("station-1");
        station.setLocation(location);
        station.setPrice(new BigDecimal("10.00"));

        BookingStatus pendingStatus = new BookingStatus(BookingStatusType.PENDING);
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2);

        when(chargingStationRepository.findById("station-1")).thenReturn(Optional.of(station));
        when(timeSlotRepository.existsSlotInRange(eq("station-1"), any(), any(), any())).thenReturn(true);
        when(bookingStatusRepository.findByName(BookingStatusType.PENDING)).thenReturn(Optional.of(pendingStatus));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);

        Booking result = bookingBusiness.createBooking("station-1", start, end, renter);

        assertNotNull(result);
        assertEquals(new BigDecimal("20.00"), result.getTotalPrice());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void getBookingById_Success_AsRenter() throws AccessDeniedException {
        // ARRANGE
        User renter = new User(); renter.setId("renter-1");
        User owner = new User(); owner.setId("owner-1");

        ChargingLocation loc = new ChargingLocation(); loc.setUser(owner);
        ChargingStation station = new ChargingStation(); station.setLocation(loc);

        Booking b = new Booking();
        b.setId("b1");
        b.setUser(renter);
        b.setStation(station);

        when(bookingRepository.findByIdWithDetails("b1")).thenReturn(Optional.of(b));

        // ACT
        Booking result = bookingBusiness.getBookingById("b1", renter);

        // ASSERT
        assertEquals(b, result);
    }

    @Test
    void getBookingById_Success_AsOwner() throws AccessDeniedException {
        // ARRANGE
        User renter = new User(); renter.setId("renter-1");
        User owner = new User(); owner.setId("owner-1");

        ChargingLocation loc = new ChargingLocation(); loc.setUser(owner);
        ChargingStation station = new ChargingStation(); station.setLocation(loc);

        Booking b = new Booking();
        b.setId("b1");
        b.setUser(renter);
        b.setStation(station);

        when(bookingRepository.findByIdWithDetails("b1")).thenReturn(Optional.of(b));

        // ACT
        Booking result = bookingBusiness.getBookingById("b1", owner);

        // ASSERT
        assertEquals(b, result);
    }

    @Test
    void getBookingById_Fail_AccessDenied() {
        // ARRANGE
        User renter = new User(); renter.setId("renter-1");
        User owner = new User(); owner.setId("owner-1");
        User hacker = new User(); hacker.setId("hacker-1");

        ChargingLocation loc = new ChargingLocation(); loc.setUser(owner);
        ChargingStation station = new ChargingStation(); station.setLocation(loc);

        Booking b = new Booking();
        b.setId("b1");
        b.setUser(renter);
        b.setStation(station);

        when(bookingRepository.findByIdWithDetails("b1")).thenReturn(Optional.of(b));

        // ACT & ASSERT
        assertThrows(AccessDeniedException.class, () ->
                bookingBusiness.getBookingById("b1", hacker)
        );
    }

    // --- TESTS DELETE BOOKING (SECURISÉ) ---

    @Test
    void deleteBooking_Success() throws AccessDeniedException {
        // ARRANGE
        User renter = new User(); renter.setId("renter-1");
        User owner = new User(); owner.setId("owner-1");
        ChargingLocation loc = new ChargingLocation(); loc.setUser(owner);
        ChargingStation station = new ChargingStation(); station.setLocation(loc);

        Booking b = new Booking();
        b.setId("b1");
        b.setUser(renter);
        b.setStation(station);
        when(bookingRepository.findByIdWithDetails("b1")).thenReturn(Optional.of(b));

        // ACT
        bookingBusiness.deleteBooking("b1", renter);

        // ASSERT
        verify(bookingRepository).delete(b);
    }

    @Test
    void deleteBooking_Fail_AccessDenied() {
        // ARRANGE
        User renter = new User(); renter.setId("renter-1");
        User owner = new User(); owner.setId("owner-1");
        User hacker = new User(); hacker.setId("hacker");

        ChargingLocation loc = new ChargingLocation(); loc.setUser(owner);
        ChargingStation station = new ChargingStation(); station.setLocation(loc);

        Booking b = new Booking();
        b.setId("b1");
        b.setUser(renter);
        b.setStation(station);

        when(bookingRepository.findByIdWithDetails("b1")).thenReturn(Optional.of(b));

        // ACT & ASSERT
        assertThrows(AccessDeniedException.class, () ->
                bookingBusiness.deleteBooking("b1", hacker)
        );

        verify(bookingRepository, never()).delete(any());
    }
}