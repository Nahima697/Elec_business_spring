package com.elec_business.controller;

import com.elec_business.business.BookingBusiness;
import com.elec_business.controller.dto.BookingRequestDto;
import com.elec_business.controller.dto.BookingResponseDto;
import com.elec_business.controller.mapper.BookingMapper;
import com.elec_business.entity.User;
import com.elec_business.service.PdfService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingBusiness bookingBusiness;
    private final BookingMapper bookingMapper;
    private final PdfService pdfService;

    @PostMapping("/bookings")
    public ResponseEntity<BookingResponseDto> addBooking(@Valid @RequestBody BookingRequestDto bookingRequestDto, @AuthenticationPrincipal User currentUser) {
        BookingResponseDto createdBooking = bookingBusiness.createBooking(bookingRequestDto.getStationId(), bookingRequestDto.getStartDate(), bookingRequestDto.getEndDate(), currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
    }

    @PostMapping("/bookings/{id}/accept")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto validateBooking(@PathVariable String id, @AuthenticationPrincipal User currentUser) {
        return bookingBusiness.acceptBooking(id, currentUser);
    }

    @PostMapping("/bookings/{id}/reject")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto rejectBooking(@PathVariable String id, @AuthenticationPrincipal User currentUser) {
        return bookingBusiness.rejectBooking(id, currentUser);

    }

    @PutMapping("/bookings/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public BookingResponseDto updateBooking(@PathVariable String id, @Valid @RequestBody BookingRequestDto bookingRequestDto, @AuthenticationPrincipal User currentUser) {
        return bookingBusiness.updateBooking(id, bookingMapper.toEntity(bookingRequestDto), currentUser);

    }

    @GetMapping("/bookings")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDto> getAllBookings() {
        return bookingBusiness.getAllBookings();
    }

    @GetMapping("/bookings/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto getBooking(@PathVariable String id, @AuthenticationPrincipal User currentUser) throws AccessDeniedException {

        return bookingBusiness.getBookingById(id, currentUser);
    }

    @DeleteMapping("/bookings/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBooking(@PathVariable String id, @AuthenticationPrincipal User currentUser) throws AccessDeniedException {
        bookingBusiness.deleteBooking(id, currentUser);
    }

    @GetMapping("/bookings/owner/me")
    public List<BookingResponseDto> getMyStationsBookings(@AuthenticationPrincipal User user) {

        return bookingBusiness.getMyBookings(user);
    }

    @GetMapping("/bookings/renter/me")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDto> getMyBookings(@AuthenticationPrincipal User user) {
        return bookingBusiness.getMyRentals(user);
    }

    @Operation(summary = "Télécharger le reçu PDF", description = "Génère un reçu PDF pour une réservation spécifique.")
    @GetMapping("/bookings/{id}/pdf")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable String id, @AuthenticationPrincipal User currentUser) throws AccessDeniedException {

        // 1. Récupérer la réservation
        BookingResponseDto booking = bookingBusiness.getBookingById(id, currentUser);

        // 2. Générer le PDF
        byte[] pdfContent = pdfService.generateBookingReceipt(booking);

        // 3. Renvoyer le fichier
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // "attachment" force le téléchargement, "inline" l'affiche dans le navigateur
        headers.setContentDispositionFormData("attachment", "recu_" + id + ".pdf");

        return ResponseEntity.ok().headers(headers).body(pdfContent);
    }
}
