package com.elec_business.service.impl;

import com.elec_business.service.EmailService;
import com.sendgrid.helpers.mail.Mail;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("test")
public class NoOpEmailService implements EmailService {

    @Override
    public void send(Mail mail) {
        // do nothing
    }
}
