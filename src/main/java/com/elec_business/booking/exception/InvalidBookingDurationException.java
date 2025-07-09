package com.elec_business.booking.exception;

public class InvalidBookingDurationException extends RuntimeException {
    public InvalidBookingDurationException(String message) {
        super(message);
    }
}
