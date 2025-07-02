package com.elec_business.service;

import com.elec_business.dto.BookingRequestDto;
import com.elec_business.dto.BookingResponseDto;
import com.elec_business.model.*;
import com.elec_business.mapper.BookingMapper;
import com.elec_business.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.AccessDeniedException;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private static Logger log = LoggerFactory.getLogger(BookingService.class);
    private final BookingRepository bookingRepository;
    private final ChargingStationRepository chargingStationRepository;
    private final NotificationRepository notificationRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final BookingMapper bookingMapper;
    private final BookingStatusRepository bookingStatusRepository;

    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, AppUser currentUser) {
        try {

            ChargingStation station = chargingStationRepository.findById(bookingRequestDto.getStationId())
                    .orElseThrow(() -> new EntityNotFoundException("Station not found"));
            Booking booking = bookingMapper.toEntity(bookingRequestDto);
            booking.setUser(currentUser);
            booking.setStation(station);

            Instant start = bookingRequestDto.getStartDate();
            Instant end = bookingRequestDto.getEndDate();

            if (end.isBefore(start)) {
                throw new IllegalArgumentException("End date must be after start date");
            }

            // Vérifie la disponibilité avant de créer le créneau
            boolean isAvailable = timeSlotRepository.isSlotAvailable(station.getId(), start, end);
            if (!isAvailable) {
                throw new IllegalStateException("La plage horaire n’est pas disponible pour cette borne.");
            }

            // Crée le créneau
            TimeSlot slot = new TimeSlot();
            slot.setStation(station);
            slot.setStartTime(start);
            slot.setEndTime(end);
            slot.setIsAvailable(false);

            // Sauvegarde le créneau et flush pour capturer l'erreur immédiatement
            try {
                timeSlotRepository.saveAndFlush(slot);
                log.info("Time slot saved successfully.");
            } catch (DataIntegrityViolationException ex) {
                log.error("Integrity violation: ", ex);
                throw new RuntimeException("Erreur lors de la sauvegarde du créneau : violation d'intégrité", ex);
            }

            // Crée le booking
            if (booking.getStatus() == null) {
                BookingStatus pendingStatus = bookingStatusRepository.findByName("PENDING")
                        .orElseThrow(() -> new EntityNotFoundException("Status PENDING not found"));
                booking.setStatus(pendingStatus);
            }

            booking.setStartDate(start);
            booking.setEndDate(end);

           //Calcul du prix total basé sur la durée de la réservation
            BigDecimal pricePerHour = station.getPrice();  // Prix par heure de la station
            long durationInHours = Duration.between(booking.getStartDate(), booking.getEndDate()).toHours();

            if (durationInHours <= 0) {
                throw new IllegalArgumentException("The booking duration must be greater than 0 hours.");
            }

            // Calcul du prix total
            BigDecimal totalPrice = pricePerHour.multiply(BigDecimal.valueOf(durationInHours));
            booking.setTotalPrice(totalPrice);

            // Définir la date de création (si nécessaire)
            if (booking.getCreatedAt() == null) {
                booking.setCreatedAt(Instant.now());
            }

           Booking savedBooking = bookingRepository.save(booking);

            // Notification envoyée après la sauvegarde du booking
            Notification notif = new Notification();
            notif.setUser(station.getLocation().getUser());
            notif.setMessage("Vous avez reçu une demande de réservation");
            notificationRepository.save(notif);

            log.info("Booking created successfully with ID: " + booking.getId());

            return new BookingResponseDto(
                    savedBooking.getId(),
                    savedBooking.getStartDate(),
                    savedBooking.getEndDate(),
                    savedBooking.getTotalPrice(),
                    savedBooking.getStatus().getName(),
                    savedBooking.getStation().getName(),
                    currentUser.getUsername()
            );
        } catch (Exception e) {
            log.error("An error occurred while creating the booking", e);
            throw new RuntimeException("Une erreur inattendue est survenue.", e);
        }
    }


    public Booking acceptBooking(UUID bookingId, AppUser currentUser) throws AccessDeniedException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        ChargingStation station = booking.getStation();

        if (!station.getLocation().getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not the owner of this station");
        }

        BookingStatus acceptedStatus = new BookingStatus();
        acceptedStatus.setId(2); // ACCEPTED
        booking.setStatus(acceptedStatus);

        bookingRepository.save(booking);

        Notification notif = new Notification();
        notif.setUser(booking.getUser());
        notif.setMessage("Votre réservation a été acceptée !");
        notificationRepository.save(notif);

        return booking;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(UUID id) {
        return bookingRepository.findBookingById(id);
    }

    public Booking updateBooking(UUID id, BookingRequestDto dto, AppUser currentUser) throws AccessDeniedException {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        if (!booking.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only update your own bookings");
        }

        if (!"PENDING".equalsIgnoreCase(booking.getStatus().getName())) {
            throw new IllegalStateException("Only pending bookings can be updated");
        }

        // Vérifier que le créneau est disponible sur la station
        boolean available = timeSlotRepository.isSlotAvailable(
                dto.getStationId(),
                dto.getStartDate(),
                dto.getEndDate());

        if (!available) {
            throw new IllegalArgumentException("Time slot is already booked or unavailable");
        }

        booking.setStartDate(dto.getStartDate());
        booking.setEndDate(dto.getEndDate());
        booking.setStation(chargingStationRepository.findById(dto.getStationId())
                .orElseThrow(() -> new EntityNotFoundException("Station not found")));

        return bookingRepository.save(booking);
    }

    public void deleteBooking(UUID id) {
        bookingRepository.deleteBookingById(id);
    }
}
