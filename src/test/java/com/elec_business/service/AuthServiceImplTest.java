package com.elec_business.service;

import com.elec_business.controller.mapper.UserMapper;
import com.elec_business.entity.RefreshToken;
import com.elec_business.entity.User;
import com.elec_business.repository.RefreshTokenRepository;
import com.elec_business.repository.UserRepository;
import com.elec_business.security.jwt.JwtUtil;
import com.elec_business.service.impl.AuthServiceImpl;
import com.elec_business.service.impl.TokenPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith; // Ajout important
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension; // Ajout important
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class) // Plus propre que MockitoAnnotations.openMocks
public class AuthServiceImplTest {

    @Mock private AuthenticationManager authManager;
    @Mock private JwtUtil jwtUtil;
    @Mock private UserMapper mapper;
    @Mock private RefreshTokenRepository tokenRepo;
    @Mock private UserRepository userRepo;

    @InjectMocks // Injecte automatiquement les mocks dans le service
    private AuthServiceImpl authService;

    // Spy nécessaire uniquement pour le test validateRefreshToken où on mock une méthode interne
    private AuthServiceImpl spyAuthService;

    @BeforeEach
    void setUp() {
        // On prépare le spy manuellement pour les tests qui en ont besoin
        spyAuthService = Mockito.spy(new AuthServiceImpl(
                authManager, jwtUtil, mapper, tokenRepo, userRepo
        ));
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
        user.setId("abc1234");

        // On simule la sauvegarde
        when(tokenRepo.save(any())).thenAnswer(invocation -> {
            RefreshToken t = invocation.getArgument(0);
            t.setId("token-999");
            return t;
        });


        String refreshToken = authService.generateRefreshToken(user);
        ResponseCookie cookie = authService.createRefreshTokenCookie(refreshToken);

        assertEquals("refresh-token", cookie.getName());
        assertTrue(cookie.getValue().contains("token-999") || cookie.getValue().contains("refresh-token"));
        assertTrue(cookie.isHttpOnly());
        verify(tokenRepo).save(any());
    }

    // -----------------------------------------
    // TEST generateRefreshToken(User user)
    // -----------------------------------------
    @Test
    void generateRefreshToken_shouldReturnTokenId() {
        User user = new User();
        user.setId("u1");

        when(tokenRepo.save(any())).thenAnswer(invocation -> {
            RefreshToken t = invocation.getArgument(0);
            t.setId("rt-123");
            return t;
        });

        String token = authService.generateRefreshToken(user);

        assertEquals("rt-123", token);
    }

    // -----------------------------------------
    // TEST validateRefreshToken()
    // -----------------------------------------
    @Test
    void validateRefreshToken_shouldReturnNewTokens() {
        // GIVEN
        User user = new User();
        user.setId("user-1");
        user.setUsername("testUser");

        RefreshToken oldToken = mock(RefreshToken.class);

        when(tokenRepo.findById("old")).thenReturn(Optional.of(oldToken));
        when(oldToken.isExpired()).thenReturn(false);
        when(oldToken.getUser()).thenReturn(user);

        doReturn("new-refresh").when(spyAuthService).generateRefreshToken(user);

        when(jwtUtil.generateToken("testUser")).thenReturn("jwt-new");

        // WHEN
        TokenPair result = spyAuthService.validateRefreshToken("old");

        // THEN
        assertEquals("new-refresh", result.getRefreshToken());
        assertEquals("jwt-new", result.getJwt());
        verify(tokenRepo).delete(oldToken);
    }
}