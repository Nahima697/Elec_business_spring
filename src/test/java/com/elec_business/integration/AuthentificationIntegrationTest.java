package com.elec_business.integration;

import com.elec_business.controller.mapper.UserMapper;
import com.elec_business.repository.RefreshTokenRepository;
import com.elec_business.repository.UserRepository;
import com.elec_business.security.jwt.JwtUtil;
import com.elec_business.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuthentificationIntegrationTest {

    @Mock
    AuthService authService;
    @Mock
    JwtUtil jwtUtil;
    @Mock
    UserMapper mapper;
    @Mock
    RefreshTokenRepository tokenRepository;
    @Mock
    UserRepository userRepo;

    @BeforeEach
    void setUp() throws Exception {

    }
    @Test
    void test() {}
}
