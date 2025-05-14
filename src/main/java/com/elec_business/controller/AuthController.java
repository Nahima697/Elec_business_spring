package com.elec_business.controller;

import com.elec_business.config.JwtUtil;
import com.elec_business.entity.AppUser;
import com.elec_business.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AppUser appUser) {
        if (appUser.getUsername() == null || appUser.getPassword() == null || appUser.getEmail() == null) {
            return ResponseEntity.badRequest().body("Missing required fields: username, email, or password.");
        }

        if (appUserRepository.existsByUsername(appUser.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
        }

        try {
            appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
            AppUser savedUser = appUserRepository.save(appUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (Exception e) {
            log.error("Error during registration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during user registration.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AppUser appUser) {
        if (appUser.getUsername() == null || appUser.getPassword() == null) {
            return ResponseEntity.badRequest().body("Missing username or password.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(appUser.getUsername(), appUser.getPassword())
            );

            if (authentication.isAuthenticated()) {
                Map<String, Object> authData = new HashMap<>();
                authData.put("token", jwtUtil.generateToken(appUser.getUsername()));
                authData.put("type", "Bearer");
                return ResponseEntity.ok(authData);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed.");
            }

        } catch (AuthenticationException e) {
            log.warn("Authentication failed for user: {}", appUser.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        } catch (Exception e) {
            log.error("Unexpected error during login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error during login.");
        }
    }
}
