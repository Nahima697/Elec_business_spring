package com.elec_business.service.exception;

public class EmailNotSentException extends RuntimeException {
    public EmailNotSentException() {
        super("L'email n'a pas été envoyé");
    }
}
