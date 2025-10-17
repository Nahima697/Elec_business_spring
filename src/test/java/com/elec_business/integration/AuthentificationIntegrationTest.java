package com.elec_business.integration;

<<<<<<< HEAD
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
=======
import org.junit.jupiter.api.Test;


public class AuthentificationIntegrationTest {
>>>>>>> 1939fc473334638ae29f95a7d0395f966f490996
    @Test
    void test() {}
}
