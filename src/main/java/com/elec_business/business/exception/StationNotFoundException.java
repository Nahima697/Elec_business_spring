package com.elec_business.business.exception;

public class StationNotFoundException extends RuntimeException {
    public StationNotFoundException() {
        super("La station est introuvable ");
    }
}
