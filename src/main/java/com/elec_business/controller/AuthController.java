package com.elec_business.controller;

import com.elec_business.config.JwtUtil;
import com.elec_business.dto.RegistrationDto;
import com.elec_business.dto.RegistrationResponseDto;
import com.elec_business.dto.UserProfileDto;
import com.elec_business.entity.AppUser;
import com.elec_business.mapper.AppUserMapper;
import com.elec_business.service.EmailVerificationService;
import com.elec_business.service.UserRegistrationService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailVerificationService emailVerificationService;
    private final AppUserMapper appUserMapper;
    private final UserRegistrationService userRegistrationService;

    @Value("${app.auth.email-verification-required:true}")
    private boolean emailVerificationRequired;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegistrationDto registrationDto) {
        AppUser registeredUser;

        try {
            // 1. Création de l'utilisateur
            registeredUser = userRegistrationService.registerUser(registrationDto);

            // 2. Envoi de l'email si activé
            if (emailVerificationRequired) {
                emailVerificationService.sendVerificationToken(
                        registeredUser.getId(),
                        registeredUser.getEmail()
                );
            }

            // 3. Création de la réponse
            RegistrationResponseDto responseDto = new RegistrationResponseDto(
                    registeredUser.getUsername(),
                    registeredUser.getEmail(),
                    emailVerificationRequired
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);

        } catch (ValidationException ve) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ve.getMessage());

        } catch (Exception e) {
            log.error("Error during registration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during user registration.");
        }
    }

    @GetMapping("/email/verify")
    public ResponseEntity<UserProfileDto> verifyEmail(
            @RequestParam("uid") UUID userId, @RequestParam("t") String token) {

        final var verifiedUser =
                emailVerificationService.verifyEmail(userId, token);

        return ResponseEntity.ok(appUserMapper.toDto(verifiedUser));
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
