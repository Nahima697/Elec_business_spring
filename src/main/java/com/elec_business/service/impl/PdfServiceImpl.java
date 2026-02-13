package com.elec_business.service.impl;

import com.elec_business.controller.dto.BookingResponseDto;
import com.elec_business.controller.mapper.BookingMapper;
import com.elec_business.entity.Booking;
import com.elec_business.service.PdfService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {

    private final BookingMapper bookingMapper;

    @Override
    public byte[] generateBookingReceipt(BookingResponseDto bookingResponseDto) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);

            document.open();

            // 1. En-tête
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Font.NORMAL);
            Paragraph title = new Paragraph("RECU DE RESERVATION", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // Espace

            // 2. Informations générales
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Booking booking = bookingMapper.responseToEntity(bookingResponseDto);
            document.add(new Paragraph("Référence : " + booking.getId(), boldFont));
            document.add(new Paragraph("Date d'émission : " + java.time.LocalDate.now(), normalFont));
            document.add(new Paragraph("--------------------------------------------------"));
            document.add(new Paragraph(" "));

            // 3. Détails Client & Station
            document.add(new Paragraph("CLIENT", boldFont));
            document.add(new Paragraph("Nom : " + booking.getUser().getUsername(), normalFont));
            document.add(new Paragraph("Email : " + booking.getUser().getEmail(), normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("STATION DE RECHARGE", boldFont));
            document.add(new Paragraph("Nom : " + booking.getStation().getName(), normalFont));
            document.add(new Paragraph("Adresse : " + booking.getStation().getLocation().getAddressLine() + ", " + booking.getStation().getLocation().getCity(), normalFont));
            document.add(new Paragraph(" "));

            // 4. Détails Réservation (Dates)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            document.add(new Paragraph("DETAIL DU CRENEAU", boldFont));
            document.add(new Paragraph("Début : " + booking.getStartDate().format(formatter), normalFont));
            document.add(new Paragraph("Fin :   " + booking.getEndDate().format(formatter), normalFont));
            document.add(new Paragraph(" "));

            // 5. Prix Total
            document.add(new Paragraph("--------------------------------------------------"));
            Paragraph pricePara = new Paragraph("PRIX TOTAL : " + booking.getTotalPrice() + " €", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
            pricePara.setAlignment(Element.ALIGN_RIGHT);
            document.add(pricePara);

            // 6. Statut
            Paragraph statusPara = new Paragraph("Statut : " + booking.getStatus().getName(), FontFactory.getFont(FontFactory.COURIER, 10));
            statusPara.setAlignment(Element.ALIGN_RIGHT);
            document.add(statusPara);

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }
}