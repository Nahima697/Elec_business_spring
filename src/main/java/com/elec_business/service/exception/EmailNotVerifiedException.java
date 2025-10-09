package com.elec_business.service.exception;

public class EmailNotVerifiedException extends RuntimeException {

    public EmailNotVerifiedException() {
        super("Your email is not verified.");
    }
}
