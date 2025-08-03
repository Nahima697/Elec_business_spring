package com.elec_business.business.impl;

import com.elec_business.business.BookingBusiness;
import com.elec_business.business.exception.AccessDeniedStationException;
import com.elec_business.entity.TimeSlot;
import com.elec_business.repository.TimeSlotRepository;
import com.elec_business.entity.Booking;
import com.elec_business.entity.BookingStatus;
import com.elec_business.repository.BookingRepository;
import com.elec_business.repository.BookingStatusRepository;
import com.elec_business.entity.ChargingStation;
import com.elec_business.repository.ChargingStationRepository;
import com.elec_business.business.eventlistener.BookingAcceptedEvent;
import com.elec_business.entity.User;
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

    // Service pour la création d'une réservation
    @Transactional
    public Booking createBooking(Booking booking, User currentUser) {

        // Vérification de l'existence de la station
        ChargingStation station = chargingStationRepository.findById(booking.getStation().getId())
                .orElseThrow(() -> new EntityNotFoundException("Station not found"));

        // Création de la réservation
        booking.setUser(currentUser);
        booking.setStation(station);

        Instant start = booking.getStartDate();
        Instant end = booking.getEndDate();

        // Vérification que la date de fin est après la date de début
        if (end.isBefore(start)) {
            throw new InvalidBookingDurationException();
        }

        // Vérification de la disponibilité du créneau
        verifyAvailability(station, booking);

        // Création du créneau horaire
        createAndSaveTimeSlot(station, booking);

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

    // Méthode pour créer et sauvegarder un créneau horaire
    public void createAndSaveTimeSlot(ChargingStation station, Booking booking) {
        TimeSlot slot = new TimeSlot();
        slot.setStation(station);
        slot.setStartTime(booking.getStartDate());
        slot.setEndTime(booking.getEndDate());
        slot.setIsAvailable(false);
        timeSlotRepository.saveAndFlush(slot);
        log.info("Time slot saved successfully.");
    }

    // Vérification de la disponibilité du créneau
    public void verifyAvailability(ChargingStation station, Booking booking) {
        boolean isAvailable = timeSlotRepository.isSlotAvailable(station.getId(), booking.getStartDate(), booking.getEndDate());
        if (!isAvailable) {
            throw new IllegalStateException("La plage horaire n’est pas disponible pour cette borne.");
        }
    }

    // Définition de l'état de la réservation à "PENDING"
    public void setBookingStatus(Booking booking) {
        if (booking.getStatus() == null) {
            BookingStatus pendingStatus = bookingStatusRepository.findByName("PENDING")
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

        BookingStatus acceptedStatus = new BookingStatus();
        acceptedStatus.setId(2); // ACCEPTED
        booking.setStatus(acceptedStatus);

        bookingRepository.save(booking);

        eventPublisher.publishEvent(
                new BookingAcceptedEvent(booking, booking.getUser())
        );

        log.info("BookingAcceptedEvent published for booking ID: {}", booking.getId());

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

    // Mise à jour d'une réservation
    public Booking updateBooking(String  id,Booking booking, User currentUser) throws AccessDeniedBookingException {
       Booking updateBooking = bookingRepository.findById(id)
                .orElseThrow(BookingNotFoundException::new);

        if (!booking.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedBookingException();
        }

        if (!"PENDING".equalsIgnoreCase(booking.getStatus().getName())) {
            throw new IllegalStateException("Only pending bookings can be updated");
        }

        // Vérification que le créneau est disponible sur la station
        verifyAvailability(booking.getStation(), booking);

        // Mise à jour des informations de la réservation
        updateBooking.setStartDate(booking.getStartDate());
        updateBooking.setEndDate(booking.getEndDate());
        updateBooking.setStation(chargingStationRepository.findById(booking.getStation().getId())
                .orElseThrow(() -> new EntityNotFoundException("Station not found")));

        return bookingRepository.save(updateBooking);
    }

    // Suppression d'une réservation
    public void deleteBooking(String id) {
        bookingRepository.deleteBookingById(id);
    }
}