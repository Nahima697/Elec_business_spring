package com.elec_business.service.impl;

import com.elec_business.entity.User;
import com.elec_business.repository.UserRepository;
import com.elec_business.service.EmailVerificationService;
import com.elec_business.service.OtpService;
import com.elec_business.service.exception.EmailNotSentException;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.sendgrid.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.time.Instant;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final OtpService otpService;

    private final UserRepository userRepository;

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Async
    public void sendVerificationToken(String userId, String email,String baseUrl) {
        try {
            final var token = otpService.generateAndStoreOtp(userId);
            final var emailVerificationUrl =
                    baseUrl + "/api/email/verify?userId=%s&t=%s".formatted(userId, token);

            Email from = new Email("nahima.toumi697@gmail.com");
            String subject = "Verify your email";
            Email to = new Email("nahima.toumi697@gmail.com");
            Content content = new Content("text/html",
                    "<p>Click below to verify your email:</p>" +
                            "<a href=\"" + emailVerificationUrl + "\">Verify Email</a>");
            Mail mail = new Mail(from, subject, to, content);

            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println("SendGrid response: " + response.getStatusCode());
            System.out.println("SendGrid body: " + response.getBody());

        } catch (IOException ex) {
            throw new EmailNotSentException();
        }
    }

    @Transactional
    public User verifyEmail(String  userId, String token) {
        if (!otpService.isOtpValid(userId, token)) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "Token invalid or expired");
        }
        otpService.deleteOtp(userId);

        final var user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(GONE,
                                "User account has been deleted or deactivated"));
     boolean userEmailVerified = user.getEmailVerified();
        if (userEmailVerified) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "Email is already verified");
        }

        user.setEmailVerified(true);
        user.setEmailVerifiedAt(Instant.now());
        userRepository.save(user);
        return user;
    }
}