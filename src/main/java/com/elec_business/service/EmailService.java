package com.elec_business.service;

import com.sendgrid.helpers.mail.Mail;

public interface EmailService {
    void send (Mail mail);
}
