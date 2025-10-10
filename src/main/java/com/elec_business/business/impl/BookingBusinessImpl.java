package com.elec_business.business.impl;

import com.elec_business.business.BookingBusiness;
import com.elec_business.business.TimeSlotBusiness;
import com.elec_business.business.eventlistener.BookingRejectedEvent;
import com.elec_business.business.exception.AccessDeniedStationException;
import com.elec_business.entity.*;
import com.elec_business.repository.TimeSlotRepository;
import com.elec_business.repository.BookingRepository;
import com.elec_business.repository.BookingStatusRepository;
import com.elec_business.repository.ChargingStationRepository;
import com.elec_business.business.eventlistener.BookingAcceptedEvent;
import com.elec_business.business.exception.BookingNotFoundException;
import com.elec_business.business.exception.InvalidBookingDurationException;
import com.elec_business.business.exception.AccessDeniedBookingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
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

    @Transactional
    public Booking createBooking(String stationId, LocalDateTime startDate, LocalDateTime endDate, User currentUser) {

        // Vérification de l'existence de la station
        ChargingStation station = chargingStationRepository.findById(stationId)
                .orElseThrow(() -> new EntityNotFoundException("Station not found"));
        if(station.getLocation().getUser().equals(currentUser)) {
            throw new IllegalArgumentException("Vous ne pouvez pas louer votre propre borne");
        }
        Booking booking = new Booking();

        booking.setUser(currentUser);
        booking.setStation(station);

        booking.setStartDate(startDate);
        booking.setEndDate(endDate);

        // Vérification que la date de fin est après la date de début
        if (endDate.isBefore(startDate)) {
            throw new InvalidBookingDurationException();
        }

        // Vérification de la disponibilité du créneau
        verifyAvailability(station, booking);

        // Définition de l'état de la réservation
        setBookingStatus(booking);

        // Calcul du prix total
        BigDecimal totalPrice = calculateTotalPrice(station, booking);
        booking.setTotalPrice(totalPrice);

        // Définir la date de création
        if (booking.getCreatedAt() == null) {
            booking.setCreatedAt(Instant.now());
        }

        // Sauvegarde de la réservation
        Booking savedBooking = bookingRepository.save(booking);

        log.info("Booking created successfully with ID: " + booking.getId());

        // Retourner la réponse
        return savedBooking;
    }


    // Vérification de la disponibilité du créneau
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

    // Définition de l'état de la réservation à "PENDING"
    public void setBookingStatus(Booking booking) {
        if (booking.getStatus() == null) {
            BookingStatus pendingStatus = bookingStatusRepository.findByName(BookingStatusType.PENDING)
                    .orElseThrow(() -> new EntityNotFoundException("Status PENDING not found"));
            booking.setStatus(pendingStatus);
        }
    }


    // Calcul du prix total basé sur la durée de la réservation
    public BigDecimal calculateTotalPrice(ChargingStation station, Booking booking) {
        BigDecimal pricePerHour = station.getPrice();
        long durationInHours = Duration.between(booking.getStartDate(), booking.getEndDate()).toHours();

        if (durationInHours <= 0) {
            throw new InvalidBookingDurationException();
        }

        return pricePerHour.multiply(BigDecimal.valueOf(durationInHours));
    }

    // Acceptation de la réservation
    @Transactional
    public Booking acceptBooking(String bookingId, User currentUser) throws AccessDeniedBookingException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(BookingNotFoundException::new);

        ChargingStation station = booking.getStation();
        if (!station.getLocation().getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedStationException();
        }

        BookingStatus acceptedStatus = bookingStatusRepository.findByName(BookingStatusType.ACCEPTED)
                .orElseThrow(() -> new EntityNotFoundException("Status ACCEPTED not found"));
        booking.setStatus(acceptedStatus);

        timeSlotBusiness.setTimeSlotAvailability(station.getId(), booking.getStartDate(), booking.getEndDate());

//        bookingRepository.save(booking);
// pas besoin de save avec Transactional
        eventPublisher.publishEvent(
                new BookingAcceptedEvent(booking, booking.getUser())
        );

        log.info("BookingAcceptedEvent published for booking ID: {}", booking.getId());

        return booking;
    }

    @Transactional
    public Booking rejectBooking(String bookingId, User currentUser) throws AccessDeniedBookingException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(BookingNotFoundException::new);

        ChargingStation station = booking.getStation();
        if (!station.getLocation().getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedStationException();
        }

        BookingStatus rejectedStatus = bookingStatusRepository.findByName(BookingStatusType.REJECTED)
                .orElseThrow(() -> new EntityNotFoundException("Status Rejected not found"));
        booking.setStatus(rejectedStatus);

        eventPublisher.publishEvent(
                new BookingRejectedEvent(booking, booking.getUser())
        );

        log.info("BookingRejectedEvent published for booking ID: {}", booking.getId());

        return booking;
    }

    // Récupération de toutes les réservations
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll().stream()
                .toList();
    }

    // Récupération d'une réservation par ID
    public Booking getBookingById(String  id) {
            return bookingRepository.findById(id).orElseThrow(BookingNotFoundException::new);
    }

    // Mise à jour d'une réservation par le locataire
    @Transactional
    public Booking updateBooking(String  id,Booking booking, User currentUser) throws AccessDeniedBookingException {
       Booking updateBooking = bookingRepository.findById(id)
                .orElseThrow(BookingNotFoundException::new);

        if (!updateBooking.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedBookingException();
        }

        if (updateBooking.getStatus() == null ||
                !BookingStatusType.PENDING.equals(updateBooking.getStatus().getName())) {
            throw new IllegalStateException("Only pending bookings can be updated");
        }

        updateBooking.setStation(chargingStationRepository.findById(booking.getStation().getId())
                .orElseThrow(() -> new EntityNotFoundException("Station not found")));


        // Vérification que le créneau est disponible sur la station
        verifyAvailability(updateBooking.getStation(), booking);

        // Mise à jour des informations de la réservation
        updateBooking.setStartDate(booking.getStartDate());
        updateBooking.setEndDate(booking.getEndDate());

        return bookingRepository.save(updateBooking);
    }

    // Suppression d'une réservation
    @Transactional
    public void deleteBooking(String id) {
        bookingRepository.deleteBookingById(id);
    }
}