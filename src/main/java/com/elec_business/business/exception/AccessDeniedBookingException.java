package com.elec_business.business.exception;

public class AccessDeniedBookingException extends RuntimeException {
    public AccessDeniedBookingException(String message) {
        super(message);
    }
}
