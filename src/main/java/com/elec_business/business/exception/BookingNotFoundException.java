package com.elec_business.business.exception;

public class BookingNotFoundException extends BusinessException {
    public BookingNotFoundException() {
        super("La réservation est introuvable ");
    }
}
