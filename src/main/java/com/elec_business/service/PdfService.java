package com.elec_business.service;

import com.elec_business.entity.Booking;

public interface PdfService {
    public byte[] generateBookingReceipt(Booking booking);
}
