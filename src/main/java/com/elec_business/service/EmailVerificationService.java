package com.elec_business.service;

import com.elec_business.entity.User;



public interface EmailVerificationService {
    void sendVerificationToken(String userId, String email,String baseUrl) ;
    void verifyEmail(String userId, String token);
}
