package com.elec_business.service;

public interface ResetPasswordEmailService {
     void sendPasswordResetToken(String userId, String email, String baseUrl);
}
