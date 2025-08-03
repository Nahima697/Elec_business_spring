package com.elec_business.business.exception;

public class InvalidBookingDurationException extends BusinessException {
    public InvalidBookingDurationException() {
        super("La duréé de réservation est incorrecte");
    }
}
