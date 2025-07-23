package com.elec_business.booking.service;

import com.elec_business.charging_station.availability.model.TimeSlot;
import com.elec_business.charging_station.availability.repository.TimeSlotRepository;
import com.elec_business.booking.mapper.BookingMapper;
import com.elec_business.booking.dto.BookingRequestDto;
import com.elec_business.booking.dto.BookingResponseDto;
import com.elec_business.booking.model.Booking;
import com.elec_business.booking.model.BookingStatus;
import com.elec_business.booking.repository.BookingRepository;
import com.elec_business.booking.repository.BookingStatusRepository;
import com.elec_business.charging_station.model.ChargingStation;
import com.elec_business.charging_station.repository.ChargingStationRepository;
import com.elec_business.notification.eventlistener.BookingAcceptedEvent;
import com.elec_business.user.model.AppUser;
import com.elec_business.booking.exception.BookingNotFoundException;
import com.elec_business.booking.exception.InvalidBookingDurationException;
import com.elec_business.booking.exception.AccessDeniedBookingException;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final static Logger log = LoggerFactory.getLogger(BookingService.class);
    private final BookingRepository bookingRepository;
    private final ChargingStationRepository chargingStationRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final BookingMapper bookingMapper;
    private final BookingStatusRepository bookingStatusRepository;
    private final ApplicationEventPublisher eventPublisher;

    // Service pour la création d'une réservation
    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, AppUser currentUser) {

        // Vérification de l'existence de la station
        ChargingStation station = chargingStationRepository.findById(bookingRequestDto.getStationId())
                .orElseThrow(() -> new EntityNotFoundException("Station not found"));

        // Création de la réservation
        Booking booking = bookingMapper.toEntity(bookingRequestDto);
        booking.setUser(currentUser);
        booking.setStation(station);

        Instant start = bookingRequestDto.getStartDate();
        Instant end = bookingRequestDto.getEndDate();

        // Vérification que la date de fin est après la date de début
        if (end.isBefore(start)) {
            throw new InvalidBookingDurationException("End date must be after start date");
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
        return bookingMapper.toDto(savedBooking);
    }

    // Méthode pour créer et sauvegarder un créneau horaire
    private void createAndSaveTimeSlot(ChargingStation station, Booking booking) {
        TimeSlot slot = new TimeSlot();
        slot.setStation(station);
        slot.setStartTime(booking.getStartDate());
        slot.setEndTime(booking.getEndDate());
        slot.setIsAvailable(false);
        timeSlotRepository.saveAndFlush(slot);
        log.info("Time slot saved successfully.");
    }

    // Vérification de la disponibilité du créneau
    private void verifyAvailability(ChargingStation station, Booking booking) {
        boolean isAvailable = timeSlotRepository.isSlotAvailable(station.getId(), booking.getStartDate(), booking.getEndDate());
        if (!isAvailable) {
            throw new IllegalStateException("La plage horaire n’est pas disponible pour cette borne.");
        }
    }

    // Définition de l'état de la réservation à "PENDING"
    private void setBookingStatus(Booking booking) {
        if (booking.getStatus() == null) {
            BookingStatus pendingStatus = bookingStatusRepository.findByName("PENDING")
                    .orElseThrow(() -> new EntityNotFoundException("Status PENDING not found"));
            booking.setStatus(pendingStatus);
        }
    }

    // Calcul du prix total basé sur la durée de la réservation
    private BigDecimal calculateTotalPrice(ChargingStation station, Booking booking) {
        BigDecimal pricePerHour = station.getPrice();
        long durationInHours = Duration.between(booking.getStartDate(), booking.getEndDate()).toHours();

        if (durationInHours <= 0) {
            throw new InvalidBookingDurationException("The booking duration must be greater than 0 hours.");
        }

        return pricePerHour.multiply(BigDecimal.valueOf(durationInHours));
    }

    // Acceptation de la réservation
    @Transactional
    public BookingResponseDto acceptBooking(UUID bookingId, AppUser currentUser) throws AccessDeniedBookingException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        ChargingStation station = booking.getStation();
        if (!station.getLocation().getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedBookingException("You are not the owner of this station");
        }

        BookingStatus acceptedStatus = new BookingStatus();
        acceptedStatus.setId(2); // ACCEPTED
        booking.setStatus(acceptedStatus);

        bookingRepository.save(booking);

        eventPublisher.publishEvent(
                new BookingAcceptedEvent(booking, booking.getUser())
        );

        log.info("BookingAcceptedEvent published for booking ID: {}", booking.getId());

        return bookingMapper.toDto(booking);
    }

    // Récupération de toutes les réservations
    public List<BookingResponseDto> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    // Récupération d'une réservation par ID
    public BookingResponseDto getBookingById(UUID id) {
        return bookingMapper.toDto(bookingRepository.findBookingById(id));
    }

    // Mise à jour d'une réservation
    public BookingResponseDto updateBooking(UUID id, BookingRequestDto dto, AppUser currentUser) throws AccessDeniedBookingException {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        if (!booking.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedBookingException("You can only update your own bookings");
        }

        if (!"PENDING".equalsIgnoreCase(booking.getStatus().getName())) {
            throw new IllegalStateException("Only pending bookings can be updated");
        }

        // Vérification que le créneau est disponible sur la station
        verifyAvailability(booking.getStation(), booking);

        // Mise à jour des informations de la réservation
        booking.setStartDate(dto.getStartDate());
        booking.setEndDate(dto.getEndDate());
        booking.setStation(chargingStationRepository.findById(dto.getStationId())
                .orElseThrow(() -> new EntityNotFoundException("Station not found")));

        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    // Suppression d'une réservation
    public void deleteBooking(UUID id) {
        bookingRepository.deleteBookingById(id);
    }
}