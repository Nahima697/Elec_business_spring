package com.elec_business.service.impl;

import com.elec_business.controller.mapper.UserMapper;
import com.elec_business.entity.RefreshToken;
import com.elec_business.entity.User;
import com.elec_business.repository.UserRepository;
import com.elec_business.repository.RefreshTokenRepository;
import com.elec_business.security.jwt.JwtUtil;
import com.elec_business.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserMapper mapper;
    private final RefreshTokenRepository tokenRepository;
    private final UserRepository userRepo;

    @Override
    public User authenticateUser(String username, String password) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        return (User) authentication.getPrincipal();
    }

    public ResponseCookie createRefreshTokenCookie(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(30));
        tokenRepository.save(refreshToken);

        return ResponseCookie.from("refresh-token", refreshToken.getId())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/api/refresh-token")
                .build();
    }
    @Override
    public String generateJwtToken(User user) {
        return jwtUtil.generateToken(user.getUsername());
    }

    @Override
    public String generateRefreshToken(String idUser) {
        RefreshToken refreshToken = new RefreshToken();
        User user = userRepo.findById(idUser).orElseThrow();
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(LocalDateTime.now().plus(30, ChronoUnit.DAYS));
        tokenRepository.save(refreshToken);
        return refreshToken.getId();
    }
    @Override
    public ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refresh-token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/api/refresh-token")
                .build();
    }
    @Override
    public TokenPair validateRefreshToken(String token) {
        RefreshToken refreshToken = tokenRepository.findById(token).orElseThrow();
        if (refreshToken.isExpired()) {
            throw new RuntimeException("Refresh token expired");
        }

        User user = refreshToken.getUser();
        tokenRepository.delete(refreshToken);
        String newToken = generateRefreshToken(user.getId());
        String jwt = jwtUtil.generateToken(user.getUsername());
        return new TokenPair(newToken, jwt);

    }

    @Transactional
    @Scheduled(initialDelay = 30000, fixedDelay = 86400000)
    void cleanExpiredTokens() {
        tokenRepository.deleteExpired();
    }

}

