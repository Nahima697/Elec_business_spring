package com.elec_business.service.impl;

import com.elec_business.service.EmailService;
import com.elec_business.service.exception.EmailNotSentException;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SendGridEmailService implements EmailService {
    @Value("${sendgrid.api.key:dummy-key}")
    private String sendGridApiKey;

    @Override
    public void send(Mail email) {
        try {
            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(email.build());
            Response response = sg.api(request);
            System.out.println("SendGrid response: " + response.getStatusCode());
            System.out.println("SendGrid body: " + response.getBody());
        } catch (IOException e) {
            throw new EmailNotSentException();
        }
    }
}
