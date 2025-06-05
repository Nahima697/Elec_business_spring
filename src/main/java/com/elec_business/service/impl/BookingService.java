package com.elec_business.service.impl;

import com.elec_business.dto.BookingRequestDto;
import com.elec_business.model.*;
import com.elec_business.mapper.BookingMapper;
import com.elec_business.repository.BookingRepository;
import com.elec_business.repository.ChargingStationRepository;
import com.elec_business.repository.NotificationRepository;
import com.elec_business.repository.TimeSlotRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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

    private final BookingRepository bookingRepository;
    private final ChargingStationRepository chargingStationRepository;
    private final NotificationRepository notificationRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final BookingMapper bookingMapper;

    public Booking createBooking(BookingRequestDto bookingRequestDto, AppUser currentUser) {
        Booking booking = bookingMapper.toEntity(bookingRequestDto);
        ChargingStation station = booking.getStation();
        booking.setUser(currentUser);

        Instant start = bookingRequestDto.getStartDate();
        Instant end = bookingRequestDto.getEndDate();

        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        boolean isAvailable = timeSlotRepository.isSlotAvailable(station.getId(), start, end);
        if (!isAvailable) {
            throw new IllegalStateException("La plage horaire n’est pas disponible pour cette borne.");
        }

        long totalMinutes = Duration.between(start, end).toMinutes();
        BigDecimal durationInHours = BigDecimal.valueOf(totalMinutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        BigDecimal pricePerHour = station.getPrice();
        BigDecimal totalPrice = durationInHours.multiply(pricePerHour);

        booking.setStartDate(start);
        booking.setEndDate(end);
        booking.setTotalPrice(totalPrice);

        BookingStatus statusEntity = new BookingStatus();
        statusEntity.setId(bookingRequestDto.getStatus());
        booking.setStatus(statusEntity);

        booking.setCreatedAt(Instant.now());
        bookingRepository.save(booking);

        if ("pending".equalsIgnoreCase(statusEntity.getName())) {
            Notification notif = new Notification();
            notif.setUser(station.getLocation().getUser());
            notif.setMessage("Vous avez reçu une demande de réservation");
            notificationRepository.save(notif);
        }

        return booking;
    }

    public Booking acceptBooking(UUID bookingId, AppUser currentUser) throws AccessDeniedException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        ChargingStation station = booking.getStation();

        if (!station.getLocation().getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not the owner of this station");
        }

        boolean isAvailable = timeSlotRepository.isSlotAvailable(
                station.getId(), booking.getStartDate(), booking.getEndDate());

        if (!isAvailable) {
            throw new IllegalStateException("Time slot already booked");
        }

        BookingStatus acceptedStatus = new BookingStatus();
        acceptedStatus.setId(2); // ACCEPTED
        booking.setStatus(acceptedStatus);

        TimeSlot slot = new TimeSlot();
        slot.setStation(station);
        slot.setStartTime(OffsetDateTime.from(booking.getStartDate()));
        slot.setEndTime(OffsetDateTime.from(booking.getEndDate()));
        slot.setIsAvailable(false);
        timeSlotRepository.save(slot);

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
