package com.elec_business.business.impl;

import com.elec_business.business.TimeSlotBusiness;
import com.elec_business.business.eventlistener.BookingAcceptedEvent;
import com.elec_business.business.eventlistener.BookingRejectedEvent;
import com.elec_business.business.exception.AccessDeniedBookingException;
import com.elec_business.business.exception.AccessDeniedStationException;
import com.elec_business.business.exception.BookingNotFoundException;
import com.elec_business.business.exception.InvalidBookingDurationException;
import com.elec_business.entity.*;
import com.elec_business.repository.BookingRepository;
import com.elec_business.repository.BookingStatusRepository;
import com.elec_business.repository.ChargingStationRepository;
import com.elec_business.repository.TimeSlotRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
        // ARRANGE
        User renter = new User(); renter.setId("renter-1");
        User owner = new User(); owner.setId("owner-1");

        ChargingLocation location = new ChargingLocation();
        location.setUser(owner);

        ChargingStation station = new ChargingStation();
        station.setId("station-1");
        station.setLocation(location);
        station.setPrice(new BigDecimal("10.00")); // 10€/h

        BookingStatus pendingStatus = new BookingStatus(BookingStatusType.PENDING);

        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2); // 2 heures

        when(chargingStationRepository.findById("station-1")).thenReturn(Optional.of(station));
        // Slot disponible
        when(timeSlotRepository.existsSlotInRange(eq("station-1"), any(), any(), any())).thenReturn(true);
        when(bookingStatusRepository.findByName(BookingStatusType.PENDING)).thenReturn(Optional.of(pendingStatus));
        // Mock save pour retourner l'objet passé
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);

        // ACT
        Booking result = bookingBusiness.createBooking("station-1", start, end, renter);

        // ASSERT
        assertNotNull(result);
        assertEquals(new BigDecimal("20.00"), result.getTotalPrice()); // 2h * 10€
        assertEquals(BookingStatusType.PENDING, result.getStatus().getName());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_Fail_OwnerCannotRentOwnStation() {
        User owner = new User(); owner.setId("owner-1");
        ChargingLocation location = new ChargingLocation();
        location.setUser(owner);
        ChargingStation station = new ChargingStation();
        station.setLocation(location);

        when(chargingStationRepository.findById("station-1")).thenReturn(Optional.of(station));

        // ACT & ASSERT
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            bookingBusiness.createBooking("station-1", LocalDateTime.now(), LocalDateTime.now().plusHours(1), owner)
        );
        assertEquals("Vous ne pouvez pas louer votre propre borne", ex.getMessage());
    }

    @Test
    void createBooking_Fail_SlotNotAvailable() {
        User renter = new User(); renter.setId("renter-1");
        User owner = new User(); owner.setId("owner-1");
        ChargingLocation location = new ChargingLocation();
        location.setUser(owner);
        ChargingStation station = new ChargingStation();
        station.setId("station-1");
        station.setLocation(location);

        when(chargingStationRepository.findById("station-1")).thenReturn(Optional.of(station));
        when(timeSlotRepository.existsSlotInRange(any(), any(), any(), any())).thenReturn(false); // Indisponible

        // ACT & ASSERT
        assertThrows(IllegalStateException.class, () ->
            bookingBusiness.createBooking("station-1", LocalDateTime.now(), LocalDateTime.now().plusHours(1), renter)
        );
    }

    @Test
    void createBooking_Fail_InvalidDuration() {
        User renter = new User(); renter.setId("renter-1");
        User owner = new User(); owner.setId("owner-1");
        ChargingLocation location = new ChargingLocation();
        location.setUser(owner);
        ChargingStation station = new ChargingStation();
        station.setId("station-1");
        station.setLocation(location);

        when(chargingStationRepository.findById("station-1")).thenReturn(Optional.of(station));

        // Fin avant Début
        LocalDateTime start = LocalDateTime.now().plusHours(2);
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        assertThrows(InvalidBookingDurationException.class, () ->
            bookingBusiness.createBooking("station-1", start, end, renter)
        );
    }

    // --- TESTS CALCULATE PRICE ---

    @Test
    void calculateTotalPrice_Success() {
        ChargingStation station = new ChargingStation();
        station.setPrice(new BigDecimal("5.50"));

        Booking booking = new Booking();
        booking.setStartDate(LocalDateTime.of(2023, 1, 1, 10, 0));
        booking.setEndDate(LocalDateTime.of(2023, 1, 1, 12, 0)); // 2 heures

        BigDecimal price = bookingBusiness.calculateTotalPrice(station, booking);

        assertEquals(new BigDecimal("11.00"), price);
    }

    @Test
    void calculateTotalPrice_Fail_ZeroDuration() {
        ChargingStation station = new ChargingStation();
        station.setPrice(BigDecimal.TEN);
        Booking booking = new Booking();
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now()); // 0 heures

        assertThrows(InvalidBookingDurationException.class, () ->
            bookingBusiness.calculateTotalPrice(station, booking)
        );
    }

    // --- TESTS ACCEPT / REJECT ---

    @Test
    void acceptBooking_Success() throws AccessDeniedBookingException {
        // ARRANGE
        User owner = new User(); owner.setId("owner-1");
        ChargingLocation location = new ChargingLocation();
        location.setUser(owner);
        ChargingStation station = new ChargingStation();
        station.setLocation(location);
        station.setId("station-1");

        Booking booking = new Booking();
        booking.setId("booking-1");
        booking.setStation(station);
        booking.setUser(new User()); // Le locataire

        BookingStatus acceptedStatus = new BookingStatus(BookingStatusType.ACCEPTED);

        when(bookingRepository.findById("booking-1")).thenReturn(Optional.of(booking));
        when(bookingStatusRepository.findByName(BookingStatusType.ACCEPTED)).thenReturn(Optional.of(acceptedStatus));

        // ACT
        Booking result = bookingBusiness.acceptBooking("booking-1", owner);

        // ASSERT
        assertEquals(BookingStatusType.ACCEPTED, result.getStatus().getName());
        verify(timeSlotBusiness).setTimeSlotAvailability(eq("station-1"), any(), any());
        verify(eventPublisher).publishEvent(any(BookingAcceptedEvent.class));
    }

    @Test
    void acceptBooking_Fail_NotOwner() {
        User hacker = new User(); hacker.setId("hacker-1");
        User owner = new User(); owner.setId("owner-1");
        
        ChargingLocation location = new ChargingLocation();
        location.setUser(owner);
        ChargingStation station = new ChargingStation();
        station.setLocation(location);

        Booking booking = new Booking();
        booking.setStation(station);

        when(bookingRepository.findById("booking-1")).thenReturn(Optional.of(booking));

        assertThrows(AccessDeniedStationException.class, () ->
            bookingBusiness.acceptBooking("booking-1", hacker)
        );
    }

    @Test
    void rejectBooking_Success() throws AccessDeniedBookingException {
        User owner = new User(); owner.setId("owner-1");
        ChargingLocation location = new ChargingLocation();
        location.setUser(owner);
        ChargingStation station = new ChargingStation();
        station.setLocation(location);

        Booking booking = new Booking();
        booking.setId("booking-1");
        booking.setStation(station);
        booking.setUser(new User());

        BookingStatus rejectedStatus = new BookingStatus(BookingStatusType.REJECTED);

        when(bookingRepository.findById("booking-1")).thenReturn(Optional.of(booking));
        when(bookingStatusRepository.findByName(BookingStatusType.REJECTED)).thenReturn(Optional.of(rejectedStatus));

        Booking result = bookingBusiness.rejectBooking("booking-1", owner);

        assertEquals(BookingStatusType.REJECTED, result.getStatus().getName());
        verify(eventPublisher).publishEvent(any(BookingRejectedEvent.class));
    }

    // --- TESTS UPDATE ---

    @Test
    void updateBooking_Success() throws AccessDeniedBookingException {
        // ARRANGE
        User renter = new User(); renter.setId("renter-1");
        Booking existingBooking = new Booking();
        existingBooking.setUser(renter);
        existingBooking.setStatus(new BookingStatus(BookingStatusType.PENDING));
        existingBooking.setStation(new ChargingStation());

        Booking updateInfo = new Booking();
        updateInfo.setStartDate(LocalDateTime.now().plusDays(1));
        updateInfo.setEndDate(LocalDateTime.now().plusDays(1).plusHours(2));
        ChargingStation station = new ChargingStation();
        station.setId("station-1");
        updateInfo.setStation(station);

        when(bookingRepository.findById("booking-1")).thenReturn(Optional.of(existingBooking));
        when(chargingStationRepository.findById("station-1")).thenReturn(Optional.of(station));
        // Slot disponible
        when(timeSlotRepository.existsSlotInRange(any(), any(), any(), any())).thenReturn(true);
        when(bookingRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        // ACT
        Booking result = bookingBusiness.updateBooking("booking-1", updateInfo, renter);

        // ASSERT
        assertEquals(updateInfo.getStartDate(), result.getStartDate());
    }

    @Test
    void updateBooking_Fail_NotRenter() {
        User renter = new User(); renter.setId("renter-1");
        User other = new User(); other.setId("other");
        Booking existingBooking = new Booking();
        existingBooking.setUser(renter);

        when(bookingRepository.findById("booking-1")).thenReturn(Optional.of(existingBooking));

        assertThrows(AccessDeniedBookingException.class, () ->
            bookingBusiness.updateBooking("booking-1", new Booking(), other)
        );
    }

    @Test
    void updateBooking_Fail_NotPending() {
        User renter = new User(); renter.setId("renter-1");
        Booking existingBooking = new Booking();
        existingBooking.setUser(renter);
        existingBooking.setStatus(new BookingStatus(BookingStatusType.ACCEPTED)); // Déjà accepté

        when(bookingRepository.findById("booking-1")).thenReturn(Optional.of(existingBooking));

        assertThrows(IllegalStateException.class, () ->
            bookingBusiness.updateBooking("booking-1", new Booking(), renter)
        );
    }

    // --- TESTS GET / DELETE ---

    @Test
    void getBookingById_Success() {
        Booking b = new Booking();
        when(bookingRepository.findById("123")).thenReturn(Optional.of(b));
        
        Booking result = bookingBusiness.getBookingById("123");
        assertEquals(b, result);
    }

    @Test
    void getBookingById_NotFound() {
        when(bookingRepository.findById("999")).thenReturn(Optional.empty());
        assertThrows(BookingNotFoundException.class, () -> bookingBusiness.getBookingById("999"));
    }

    @Test
    void deleteBooking_Success() {
        doNothing().when(bookingRepository).deleteBookingById("123");
        bookingBusiness.deleteBooking("123");
        verify(bookingRepository).deleteBookingById("123");
    }

    @Test
    void getMyRentals_Success() {
        User u = new User(); u.setId("u1");
        when(bookingRepository.findByUserId("u1")).thenReturn(List.of(new Booking()));
        
        List<Booking> result = bookingBusiness.getMyRentals(u);
        assertFalse(result.isEmpty());
    }
}
