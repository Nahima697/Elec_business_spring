package com.elec_business.service.impl;

import com.elec_business.controller.dto.LoginCredentialsDTO;
import com.elec_business.controller.dto.LoginResponseDTO;
import com.elec_business.controller.mapper.UserMapper;
import com.elec_business.entity.RefreshToken;
import com.elec_business.entity.User;
import com.elec_business.repository.UserRepository;
import com.elec_business.repository.RefreshTokenRepository;
import com.elec_business.security.jwt.JwtUtil;
import com.elec_business.service.AuthService;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {
    private AuthenticationManager authManager;
    private JwtUtil jwtUtil;
    private UserMapper mapper;
    private RefreshTokenRepository tokenRepository;
    private UserRepository userRepo;

    @Override
    public LoginResponseDTO login(LoginCredentialsDTO credentials) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        credentials.getEmail(),
                        credentials.getPassword()));
        User user = (User) authentication.getPrincipal();

        String token = jwtUtil.generateToken(user.getUsername());
        return new LoginResponseDTO(token, mapper.toDTO(user));
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
    @Scheduled(fixedDelay = 24, timeUnit = TimeUnit.HOURS)
    void cleanExpiredTokens() {
        tokenRepository.deleteExpired();
    }

}

