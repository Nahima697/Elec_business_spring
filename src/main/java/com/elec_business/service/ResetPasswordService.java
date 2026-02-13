package com.elec_business.service;

public interface ResetPasswordService {
    void sendPasswordResetToken(String userId, String email, String baseUrl);

    void resetPassword( String userId, String token, String newPassword);
}
