package com.elec_business.booking.exception;

public class AccessDeniedBookingException extends RuntimeException {
    public AccessDeniedBookingException(String message) {
        super(message);
    }
}
