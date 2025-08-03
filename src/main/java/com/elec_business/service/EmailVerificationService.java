package com.elec_business.service;

import com.elec_business.entity.User;

import java.util.UUID;

public interface EmailVerificationService {
    void sendVerificationToken(String userId, String email);
    User verifyEmail(String userId, String token);
}
