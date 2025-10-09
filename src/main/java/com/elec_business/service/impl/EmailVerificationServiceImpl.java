package com.elec_business.service.impl;

import com.elec_business.entity.User;
import com.elec_business.repository.UserRepository;
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
    public void sendVerificationToken(String userId, String email) throws IOException {
        // Générer le token OTP
        final var token = otpService.generateAndStoreOtp(userId);

        final String baseUrl = "https://elec-business-spring.onrender.com";
        final var emailVerificationUrl = baseUrl + "/api/email/verify?userId=%s&t=%s"
                .formatted(userId, token);

        // Construire l'email
        Email from = new Email("noreply@electricity-business.com"); // Doit être une adresse vérifiée dans SendGrid
        String subject = "Verify your email";
        Email to = new Email(email);

        String htmlContent = """
                    <p>Click below to verify your email:</p>
                    <a href="%s">Verify Email</a>
                """.formatted(emailVerificationUrl);

        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, to, content);

        // Envoyer via SendGrid
        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sg.api(request);
        } catch (IOException ex) {
            throw ex;
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