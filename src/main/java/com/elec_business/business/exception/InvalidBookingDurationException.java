package com.elec_business.business.exception;

public class InvalidBookingDurationException extends RuntimeException {
    public InvalidBookingDurationException(String message) {
        super(message);
    }
}
