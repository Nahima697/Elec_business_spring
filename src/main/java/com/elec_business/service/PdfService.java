package com.elec_business.service;

import com.elec_business.controller.dto.BookingResponseDto;


public interface PdfService {
    public byte[] generateBookingReceipt(BookingResponseDto booking);
}
