package com.elec_business.service.impl;

import com.elec_business.service.EmailService;
import com.sendgrid.helpers.mail.Mail;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnMissingBean(EmailService.class)
public class NoOpEmailService implements EmailService {

    @Override
    public void send(Mail mail) {
        // do nothing
    }
}
