package com.elec_business.service;

import com.elec_business.entity.RefreshToken;
import com.elec_business.entity.User;
import com.elec_business.repository.RefreshTokenRepository;
import com.elec_business.security.jwt.JwtUtil;
import com.elec_business.service.impl.AuthServiceImpl;
import com.elec_business.service.impl.TokenPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private AuthenticationManager authManager;
    @Mock private JwtUtil jwtUtil;
    @Mock private RefreshTokenRepository tokenRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    private AuthServiceImpl spyAuthService;

    @BeforeEach
    void setUp() {
        spyAuthService = Mockito.spy(new AuthServiceImpl(
                authManager,
                jwtUtil,
                tokenRepository
        ));
    }

    // ================================
    // authenticateUser
    // ================================

    @Test
    void authenticateUser_shouldReturnUser() {

        User user = new User();
        user.setUsername("john");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(authManager.authenticate(any()))
                .thenReturn(authentication);

        User result = authService.authenticateUser("john", "password");

        assertEquals("john", result.getUsername());
        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    // ================================
    // generateJwtToken
    // ================================

    @Test
    void generateJwtToken_shouldReturnToken() {

        User user = new User();
        user.setUsername("john");

        when(jwtUtil.generateToken("john"))
                .thenReturn("jwt-token");

        String token = authService.generateJwtToken(user);

        assertEquals("jwt-token", token);
    }

    // ================================
    // generateRefreshToken
    // ================================

    @Test
    void generateRefreshToken_shouldReturnTokenId() {

        User user = new User();

        when(tokenRepository.save(any()))
                .thenAnswer(invocation -> {
                    RefreshToken token = invocation.getArgument(0);
                    token.setId("rt-123");
                    return token;
                });

        String token = authService.generateRefreshToken(user);

        assertEquals("rt-123", token);
        verify(tokenRepository).save(any());
    }

    // ================================
    // createRefreshTokenCookie
    // ================================

    @Test
    void createRefreshTokenCookie_shouldCreateSecureCookie() {

        ResponseCookie cookie =
                authService.createRefreshTokenCookie("token-999");

        assertEquals("refresh_token", cookie.getName());
        assertEquals("token-999", cookie.getValue());
        assertTrue(cookie.isHttpOnly());
        assertTrue(cookie.isSecure());
        assertEquals("None", cookie.getSameSite());
    }

    // ================================
    // validateRefreshToken
    // ================================

    @Test
    void validateRefreshToken_shouldRotateTokens() {

        User user = new User();
        user.setUsername("testUser");

        RefreshToken oldToken = new RefreshToken();
        oldToken.setUser(user);
        oldToken.setExpiresAt(LocalDateTime.now().plusDays(1));

        when(tokenRepository.findById("old"))
                .thenReturn(Optional.of(oldToken));

        doReturn("new-refresh")
                .when(spyAuthService)
                .generateRefreshToken(user);

        when(jwtUtil.generateToken("testUser"))
                .thenReturn("jwt-new");

        TokenPair result =
                spyAuthService.validateRefreshToken("old");

        assertEquals("new-refresh", result.getRefreshToken());
        assertEquals("jwt-new", result.getJwt());

        verify(tokenRepository).delete(oldToken);
    }
}
