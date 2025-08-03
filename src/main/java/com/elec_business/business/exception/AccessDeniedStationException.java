package com.elec_business.business.exception;

public class AccessDeniedStationException extends BusinessException {
    public AccessDeniedStationException() {
        super("Vous n'êtes pas propriétaire de cette station");
    }
}
