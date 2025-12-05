package com.elec_business.service;

import com.elec_business.controller.mapper.UserMapper;
import com.elec_business.entity.RefreshToken;
import com.elec_business.entity.User;
import com.elec_business.repository.RefreshTokenRepository;
import com.elec_business.repository.UserRepository;
import com.elec_business.security.jwt.JwtUtil;
import com.elec_business.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.elec_business.service.impl.TokenPair;
import org.mockito.*;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authManager;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserMapper mapper;
    @Mock
    private RefreshTokenRepository tokenRepo;
    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // -----------------------------------------
    // TEST authenticateUser()
    // -----------------------------------------
    @Test
    void authenticateUser_shouldReturnUser() {
        User user = new User();
        user.setUsername("john");

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(authManager.authenticate(any())).thenReturn(auth);

        User result = authService.authenticateUser("john", "password");

        assertEquals("john", result.getUsername());
        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    // -----------------------------------------
    // TEST generateJwtToken()
    // -----------------------------------------
    @Test
    void generateJwtToken_shouldReturnToken() {
        User user = new User();
        user.setUsername("john");
        when(jwtUtil.generateToken("john")).thenReturn("jwt-token");

        String token = authService.generateJwtToken(user);

        assertEquals("jwt-token", token);
    }

    // -----------------------------------------
    // TEST createRefreshTokenCookie(User user)
    // -----------------------------------------
    @Test
    void createRefreshTokenCookie_shouldCreateCookie() {
        User user = new User();
        user.setId("abc123");

        RefreshToken token = new RefreshToken();
        token.setId("token-999");
        token.setUser(user);

        when(tokenRepo.save(any())).thenAnswer(invocation -> {
            RefreshToken t = invocation.getArgument(0);
            t.setId("token-999");
            return t;
        });

        ResponseCookie cookie = authService.createRefreshTokenCookie(user);

        assertEquals("refresh-token", cookie.getName());
        assertEquals("token-999", cookie.getValue());
        assertTrue(cookie.isHttpOnly());
        verify(tokenRepo).save(any());
    }

    // -----------------------------------------
    // TEST generateRefreshToken(String idUser)
    // -----------------------------------------
    @Test
    void generateRefreshToken_shouldReturnTokenId() {
        User user = new User();
        user.setId("u1");

        when(userRepo.findById("u1")).thenReturn(Optional.of(user));
        when(tokenRepo.save(any())).thenAnswer(invocation -> {
            RefreshToken t = invocation.getArgument(0);
            t.setId("rt-123");
            return t;
        });

        String token = authService.generateRefreshToken("u1");

        assertEquals("rt-123", token);
    }

    // -----------------------------------------
    // TEST validateRefreshToken()
    // -----------------------------------------
    @Test
    void validateRefreshToken_shouldReturnNewTokens() {
        User user = new User();
        user.setId("u1");
        user.setUsername("john");

        RefreshToken oldToken = new RefreshToken();
        oldToken.setId("old");
        oldToken.setUser(user);
        oldToken.setExpiresAt(LocalDateTime.now().plusDays(1));

        when(tokenRepo.findById("old")).thenReturn(Optional.of(oldToken));
        when(jwtUtil.generateToken("john")).thenReturn("jwt-new");

        when(tokenRepo.save(any())).thenAnswer(invocation -> {
            RefreshToken t = invocation.getArgument(0);
            t.setId("new-refresh");
            return t;
        });

        TokenPair result = authService.validateRefreshToken("old");

        assertEquals("new-refresh", result.getRefreshToken());
        assertEquals("jwt-new", result.getJwt());
        verify(tokenRepo).delete(oldToken);
    }
}
