package com.elec_business.service.impl;

import com.elec_business.service.EmailService;
import com.elec_business.service.OtpService;
import com.elec_business.service.ResetPasswordEmailService;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResetPasswordEmailServiceImpl implements ResetPasswordEmailService {
    private final OtpService otpService;
    private final EmailService emailService;
    @Value("${FROM_EMAIL}")
    private String fromEmail;

    @Override
    @Async
    public void sendPasswordResetToken(String userId, String email, String baseUrl) {
        final var token = otpService.generateOtp();
        otpService.store("otp:forgotten-password:%s".formatted(userId), token);
        final var emailResetPasswordUrl =
                baseUrl + "/api/email/reset-password?userId=%s&t=%s".formatted(userId, token);

        Email from = new Email(fromEmail);
        String subject = "Réinitialisation de mot de passe";

        Email to = new Email(email);
        Content content = new Content("text/html",
                "<p>CCliquer pour réinitialiser le mot de passe:</p>" +
                        "<a href=\"" + emailResetPasswordUrl + "\">Cliquer pour réiniatiliser votre mot de passe</a>");
        Mail mail = new Mail(from, subject, to, content);
        this.emailService.send(mail);
    }
}
