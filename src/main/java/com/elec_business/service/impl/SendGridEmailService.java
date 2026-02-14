package com.elec_business.service.impl;

import com.elec_business.service.EmailService;
import com.elec_business.service.exception.EmailNotSentException;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Profile("!test")
@RequiredArgsConstructor
public class SendGridEmailService implements EmailService {

    private final SendGrid sendGrid;

    @Override
    public void send(Mail email) {
        try {
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(email.build());

            Response response = sendGrid.api(request);

            System.out.println("SendGrid status: " + response.getStatusCode());
            System.out.println("SendGrid body: " + response.getBody());

            if (response.getStatusCode() >= 400) {
                throw new RuntimeException("SendGrid error: " + response.getBody());
            }

        } catch (IOException e) {
            throw new EmailNotSentException();
        }
    }
}
