package com.elec_business.service.impl;

import com.elec_business.entity.RefreshToken;
import com.elec_business.entity.User;
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
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository tokenRepository;

    @Override
    public User authenticateUser(String username, String password) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        return (User) authentication.getPrincipal();
    }

    @Override
    public String generateJwtToken(User user) {
        return jwtUtil.generateToken(user.getUsername());
    }

    @Override
    public ResponseCookie createAccessTokenCookie(String jwt) {
        return ResponseCookie.from("access_token", jwt)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(15 * 60)
                .build();
    }


    @Override
    public String generateRefreshToken(User user) {

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(30));

        tokenRepository.save(refreshToken);

        return refreshToken.getId();
    }

    @Override
    public ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(30L * 24 * 60 * 60)
                .build();
    }


    @Override
    @Transactional
    public TokenPair validateRefreshToken(String token) {

        RefreshToken refreshToken = tokenRepository.findById(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (refreshToken.isExpired()) {
            tokenRepository.delete(refreshToken);
            throw new IllegalStateException("Refresh token expired");
        }

        User user = refreshToken.getUser();

        tokenRepository.delete(refreshToken);

        String newRefreshToken = generateRefreshToken(user);
        String newJwt = jwtUtil.generateToken(user.getUsername());

        return new TokenPair(newRefreshToken, newJwt);
    }


    @Transactional
    @Scheduled(fixedDelay = 24, timeUnit = TimeUnit.HOURS)
    void cleanExpiredTokens() {
        tokenRepository.deleteExpired();
    }
}
