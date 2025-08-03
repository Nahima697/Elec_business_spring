package com.elec_business.business.exception;

public class AccessDeniedBookingException extends RuntimeException {
    public AccessDeniedBookingException() {
        super("Vous n'êtes pas propriétaire de cette réservation");
    }
}
