package com.elec_business.service.impl;

import com.elec_business.entity.User;
import com.elec_business.repository.UserRepository;
import com.elec_business.service.EmailVerificationService;
import com.elec_business.service.OtpService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final OtpService otpService;

    private final UserRepository userRepository;

    private final JavaMailSender mailSender;

    @Async
    public void sendVerificationToken(String  userId, String email) {
        final var token = otpService.generateAndStoreOtp(userId);

        // Localhost URL with userId and OTP token
        final var emailVerificationUrl =
                "http://localhost:8080/api/email/verify?userId=%s&t=%s"
                        .formatted(userId, token);

        final var emailText =
                "Click the link to verify your email: " + emailVerificationUrl;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Email Verification");
        message.setFrom("System");
        message.setText(emailText);

        mailSender.send(message);
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