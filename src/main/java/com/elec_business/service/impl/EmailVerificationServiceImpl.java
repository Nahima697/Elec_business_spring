package com.elec_business.service.impl;

import com.elec_business.repository.UserRepository;
import com.elec_business.service.EmailService;
import com.elec_business.service.EmailVerificationService;
import com.elec_business.service.OtpService;
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

import java.time.Instant;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {
    @Value("${FROM_EMAIL}")
    private String fromEmail;
    private final OtpService otpService;
    private final EmailService emailService;
    private final UserRepository userRepository;

    @Override
    @Async
    public void sendVerificationToken(String userId, String email, String baseUrl) {
        final var token = otpService.generateOtp();
        otpService.store(userId, token, "verify-email");
        final var emailVerificationUrl =
                baseUrl + "/api/email/verify?userId=%s&t=%s".formatted(userId, token);

        Email from = new Email(fromEmail);
        String subject = "Verify your email";
        Email to = new Email(email);
        Content content = new Content("text/html",
                "<p>Click below to verify your email:</p>" +
                        "<a href=\"" + emailVerificationUrl + "\">Verify Email</a>");
        Mail mail = new Mail(from, subject, to, content);
        this.emailService.send(mail);
    }

    @Override
    @Transactional
    public void verifyEmail(String userId, String token) {

        boolean isValid = otpService.isOtpValid(userId, token, "verify-email");

        if (!isValid) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "Token invalid or expired"
            );
        }

        final var user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                GONE,
                                "User account has been deleted or deactivated"
                        )
                );

        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "Email is already verified"
            );
        }

        user.setEmailVerified(true);
        user.setEmailVerifiedAt(Instant.now());

        otpService.deleteOtp(userId, "verify-email");
    }

}
