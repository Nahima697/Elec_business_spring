package com.elec_business.business.impl;

import com.elec_business.business.TimeSlotBusiness;
import com.elec_business.controller.dto.BookingResponseDto;
import com.elec_business.controller.mapper.BookingMapper;
import com.elec_business.entity.*;
import com.elec_business.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingBusinessTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private ChargingStationRepository chargingStationRepository;
    @Mock private TimeSlotRepository timeSlotRepository;
    @Mock private BookingStatusRepository bookingStatusRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private TimeSlotBusiness timeSlotBusiness;
    @Mock private BookingMapper bookingMapper;

    @InjectMocks
    private BookingBusinessImpl bookingBusiness;

    @Test
    void createBooking_Success() {

        User renter = new User(); renter.setId("renter-1");
        User owner = new User(); owner.setId("owner-1");

        ChargingLocation location = new ChargingLocation();
        location.setUser(owner);

        ChargingStation station = new ChargingStation();
        station.setId("station-1");
        station.setLocation(location);
        station.setPrice(new BigDecimal("10.00"));

        BookingStatus pendingStatus = new BookingStatus(BookingStatusType.PENDING);

        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2);

        when(chargingStationRepository.findById("station-1"))
                .thenReturn(Optional.of(station));

        when(timeSlotRepository.existsSlotInRange(eq("station-1"), any(), any(), eq("[]")))
                .thenReturn(true);

        when(bookingStatusRepository.findByName(BookingStatusType.PENDING))
                .thenReturn(Optional.of(pendingStatus));

        // save retourne l'entité passée
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(i -> i.getArgument(0));

        // mapper retourne un mock (on ne teste pas le mapping ici)
        when(bookingMapper.toResponseDto(any(Booking.class)))
                .thenReturn(mock(BookingResponseDto.class));

        bookingBusiness.createBooking("station-1", start, end, renter);

        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(captor.capture());

        Booking saved = captor.getValue();
        assertNotNull(saved.getTotalPrice());
        assertEquals(0, new BigDecimal("20.00").compareTo(saved.getTotalPrice())); // 10€/h * 2h
    }

    @Test
    void getBookingById_Fail_AccessDenied() {

        User renter = new User(); renter.setId("renter-1");
        User owner = new User(); owner.setId("owner-1");
        User hacker = new User(); hacker.setId("hacker-1");

        ChargingLocation loc = new ChargingLocation(); loc.setUser(owner);
        ChargingStation station = new ChargingStation(); station.setLocation(loc);

        Booking b = new Booking();
        b.setId("b1");
        b.setUser(renter);
        b.setStation(station);

        when(bookingRepository.findByIdWithDetails("b1"))
                .thenReturn(Optional.of(b));

        assertThrows(AccessDeniedException.class, () ->
                bookingBusiness.getBookingById("b1", hacker)
        );
    }

    @Test
    void deleteBooking_Success() throws AccessDeniedException {

        User renter = new User(); renter.setId("renter-1");
        User owner = new User(); owner.setId("owner-1");

        ChargingLocation loc = new ChargingLocation(); loc.setUser(owner);
        ChargingStation station = new ChargingStation(); station.setLocation(loc);

        Booking b = new Booking();
        b.setId("b1");
        b.setUser(renter);
        b.setStation(station);

        when(bookingRepository.findByIdWithDetails("b1"))
                .thenReturn(Optional.of(b));

        bookingBusiness.deleteBooking("b1", renter);

        verify(bookingRepository).delete(b);
        verifyNoInteractions(bookingMapper);
    }

    @Test
    void deleteBooking_Fail_AccessDenied() {

        User renter = new User(); renter.setId("renter-1");
        User owner = new User(); owner.setId("owner-1");
        User hacker = new User(); hacker.setId("hacker");

        ChargingLocation loc = new ChargingLocation(); loc.setUser(owner);
        ChargingStation station = new ChargingStation(); station.setLocation(loc);

        Booking b = new Booking();
        b.setId("b1");
        b.setUser(renter);
        b.setStation(station);

        when(bookingRepository.findByIdWithDetails("b1"))
                .thenReturn(Optional.of(b));

        assertThrows(AccessDeniedException.class, () ->
                bookingBusiness.deleteBooking("b1", hacker)
        );

        verify(bookingRepository, never()).delete(any());
        verifyNoInteractions(bookingMapper);
    }
}
