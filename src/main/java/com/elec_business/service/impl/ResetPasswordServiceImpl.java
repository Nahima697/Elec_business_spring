package com.elec_business.service.impl;

import com.elec_business.entity.User;
import com.elec_business.repository.UserRepository;
import com.elec_business.service.EmailService;
import com.elec_business.service.OtpService;
import com.elec_business.service.ResetPasswordService;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Content;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.GONE;

@Service
@RequiredArgsConstructor
public class ResetPasswordServiceImpl implements ResetPasswordService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${FROM_EMAIL}")
    private String fromEmail;

    @Override
    @Async
    public void sendPasswordResetToken(String userId, String email, String baseUrl) {

        String token = otpService.generateOtp();

        otpService.store(userId, token, "reset-password");

        String resetUrl =
                baseUrl + "/api/reset-password/confirm?userId=%s&t=%s"
                        .formatted(userId, token);

        Mail mail = new Mail(
                new Email(fromEmail),
                "Réinitialisation de mot de passe",
                new Email(email),
                new Content(
                        "text/html",
                        "<p>Cliquez pour réinitialiser votre mot de passe :</p>" +
                                "<a href=\"" + resetUrl + "\">Réinitialiser</a>"
                )
        );

        emailService.send(mail);
    }

    @Override
    @Transactional
    public void resetPassword(String userId, String token, String newPassword) {

        boolean isValid =
                otpService.isOtpValid(userId, token, "reset-password");

        if (!isValid) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid or expired token"
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.GONE,
                                "User not found"
                        )
                );

        user.setPassword(passwordEncoder.encode(newPassword));

        otpService.deleteOtp(userId, "reset-password");
    }
}
