package com.elec_business.business.impl;

import com.elec_business.business.BookingBusiness;
import com.elec_business.business.TimeSlotBusiness;
import com.elec_business.business.eventlistener.BookingAcceptedEvent;
import com.elec_business.business.eventlistener.BookingRejectedEvent;
import com.elec_business.business.exception.*;
import com.elec_business.controller.dto.BookingResponseDto;
import com.elec_business.controller.mapper.BookingMapper;
import com.elec_business.entity.*;
import com.elec_business.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingBusinessImpl implements BookingBusiness {

    private static final Logger log = LoggerFactory.getLogger(BookingBusinessImpl.class);

    private final BookingRepository bookingRepository;
    private final ChargingStationRepository chargingStationRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final BookingStatusRepository bookingStatusRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final TimeSlotBusiness timeSlotBusiness;
    private final BookingMapper bookingMapper;

    @Transactional
    @Override
    public BookingResponseDto createBooking(String stationId, LocalDateTime startDate, LocalDateTime endDate, User currentUser) {

        ChargingStation station = chargingStationRepository.findById(stationId)
                .orElseThrow(() -> new EntityNotFoundException("Station not found"));

        if (station.getLocation().getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Vous ne pouvez pas louer votre propre borne");
        }

        if (endDate.isBefore(startDate) || endDate.isEqual(startDate)) {
            throw new InvalidBookingDurationException();
        }

        Booking booking = new Booking();
        booking.setUser(currentUser);
        booking.setStation(station);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);

        verifyAvailability(station, booking);
        setBookingStatus(booking);

        booking.setTotalPrice(calculateTotalPrice(station, booking));

        if (booking.getCreatedAt() == null) {
            booking.setCreatedAt(Instant.now());
        }

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created successfully with ID: {}", savedBooking.getId());

        return bookingMapper.toResponseDto(savedBooking);
    }

    @Override
    public void verifyAvailability(ChargingStation station, Booking booking) {
        boolean slotAvailable = timeSlotRepository.existsSlotInRange(
                station.getId(),
                booking.getStartDate(),
                booking.getEndDate(),
                "[]"
        );
        if (!slotAvailable) {
            throw new IllegalStateException("No available slot for the given period.");
        }
    }

    public void setBookingStatus(Booking booking) {
        if (booking.getStatus() == null) {
            BookingStatus pendingStatus = bookingStatusRepository.findByName(BookingStatusType.PENDING)
                    .orElseThrow(() -> new EntityNotFoundException("Status PENDING not found"));
            booking.setStatus(pendingStatus);
        }
    }

    @Override
    public BigDecimal calculateTotalPrice(ChargingStation station, Booking booking) {
        BigDecimal pricePerHour = station.getPrice();
        long durationInMinutes = Duration.between(booking.getStartDate(), booking.getEndDate()).toMinutes();
        if (durationInMinutes <= 0) throw new InvalidBookingDurationException();

        BigDecimal durationInHours = BigDecimal.valueOf(durationInMinutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.CEILING);

        return pricePerHour.multiply(durationInHours);
    }

    @Transactional
    @Override
    public BookingResponseDto acceptBooking(String bookingId, User currentUser) {
        Booking booking = bookingRepository.findByIdWithDetails(bookingId)
                .orElseThrow(BookingNotFoundException::new);

        ChargingStation station = booking.getStation();
        if (!station.getLocation().getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedStationException();
        }

        BookingStatus acceptedStatus = bookingStatusRepository.findByName(BookingStatusType.ACCEPTED)
                .orElseThrow(() -> new EntityNotFoundException("Status ACCEPTED not found"));
        booking.setStatus(acceptedStatus);

        timeSlotBusiness.setTimeSlotAvailability(station.getId(), booking.getStartDate(), booking.getEndDate());

        eventPublisher.publishEvent(new BookingAcceptedEvent(booking));

        return bookingMapper.toResponseDto(booking);
    }

    @Transactional
    @Override
    public BookingResponseDto rejectBooking(String bookingId, User currentUser) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(BookingNotFoundException::new);

        ChargingStation station = booking.getStation();
        if (!station.getLocation().getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedStationException();
        }

        BookingStatus rejectedStatus = bookingStatusRepository.findByName(BookingStatusType.REJECTED)
                .orElseThrow(() -> new EntityNotFoundException("Status REJECTED not found"));
        booking.setStatus(rejectedStatus);

        eventPublisher.publishEvent(new BookingRejectedEvent(booking));

        return bookingMapper.toResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getAllBookings() {
        return bookingMapper.toDtos(bookingRepository.findAll());
    }

    /**
     * ✅ Méthode interne : charge l'entité + vérifie les droits
     */
    private Booking getBookingEntityByIdAndCheckAccess(String id, User currentUser) throws AccessDeniedException {
        Booking booking = bookingRepository.findByIdWithDetails(id)
                .orElseThrow(BookingNotFoundException::new);

        boolean isRenter = booking.getUser().getId().equals(currentUser.getId());
        boolean isOwner = booking.getStation().getLocation().getUser().getId().equals(currentUser.getId());

        if (!isRenter && !isOwner) {
            throw new AccessDeniedException("Vous n'avez pas les droits pour accéder à cette réservation.");
        }

        return booking;
    }

    @Override
    public BookingResponseDto getBookingById(String id, User currentUser) throws AccessDeniedException {
        return bookingMapper.toResponseDto(getBookingEntityByIdAndCheckAccess(id, currentUser));
    }

    @Override
    @Transactional
    public List<BookingResponseDto> getMyBookings(User user) {
        return bookingMapper.toDtos(bookingRepository.findByStationOwner(user.getId()));
    }

    @Transactional
    @Override
    public BookingResponseDto updateBooking(String id, Booking booking, User currentUser) {
        Booking updateBooking = bookingRepository.findById(id)
                .orElseThrow(BookingNotFoundException::new);

        if (!updateBooking.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedBookingException();
        }

        if (updateBooking.getStatus() == null || !BookingStatusType.PENDING.equals(updateBooking.getStatus().getName())) {
            throw new IllegalStateException("Only pending bookings can be updated");
        }

        updateBooking.setStation(chargingStationRepository.findById(booking.getStation().getId())
                .orElseThrow(() -> new EntityNotFoundException("Station not found")));

        verifyAvailability(updateBooking.getStation(), booking);

        updateBooking.setStartDate(booking.getStartDate());
        updateBooking.setEndDate(booking.getEndDate());

        return bookingMapper.toResponseDto(bookingRepository.save(updateBooking));
    }

    @Transactional
    @Override
    public void deleteBooking(String id, User currentUser) throws AccessDeniedException {
        Booking booking = getBookingEntityByIdAndCheckAccess(id, currentUser);
        bookingRepository.delete(booking);
        log.info("Booking {} deleted by user {}", id, currentUser.getId());
    }

    @Override
    @Transactional
    public List<BookingResponseDto> getMyRentals(User user) {
        return bookingMapper.toDtos(bookingRepository.findByRenterId(user.getId()));
    }
}
