package com.elec_business.controller;

import com.elec_business.config.JwtUtil;
import com.elec_business.dto.RegistrationDto;
import com.elec_business.dto.RegistrationResponseDto;
import com.elec_business.dto.UserRegisterDto;
import com.elec_business.model.AppUser;
import com.elec_business.exception.EmailNotVerifiedException;
import com.elec_business.mapper.AppUserMapper;
import com.elec_business.mapper.RegistrationResponseMapper;
import com.elec_business.repository.AppUserRepository;
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
import java.util.Optional;
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
    private final RegistrationResponseMapper registrationResponseMapper;
    private final AppUserRepository appUserRepository;

    @Value("${app.auth.email-verification-required:true}")
    private boolean emailVerificationRequired;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegistrationDto registrationDto) {
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
            RegistrationResponseDto responseDto = registrationResponseMapper.toDto(registeredUser);
            return ResponseEntity.status(HttpStatus.CREATED).body("Votre compté a été créé avec succès" + responseDto);

        } catch (ValidationException ve) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ve.getMessage());

        } catch (Exception e) {
            log.error("Error during registration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during user registration.");
        }
    }

    @GetMapping("/email/verify")
    public ResponseEntity<UserRegisterDto> verifyEmail(
            @RequestParam("uid") UUID userId, @RequestParam("t") String token) {
        final var verifiedUser =
                emailVerificationService.verifyEmail(userId, token);

        return ResponseEntity.ok(appUserMapper.toDto(verifiedUser));
    }

    @PostMapping("/email/resend")
    public ResponseEntity<String> resendEmailVerification(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        Optional<AppUser> appUser = appUserRepository.findByEmail(email);

        if (appUser.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid or already verified user.");
        }

        emailVerificationService.sendVerificationToken(appUser.get().getId(), appUser.get().getEmail());
        return ResponseEntity.ok("Verification email resent.");
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AppUser appUser) {
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
                return ResponseEntity.ok(authData.toString());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed.");
            }

        } catch (AuthenticationException e) {
            Throwable cause = e.getCause();

            if (cause instanceof EmailNotVerifiedException) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body("Email not verified.");
            }

            log.warn("Authentication failed for user: {}", appUser.getUsername(), e);
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password.");
        }
        catch (Exception e) {
            log.error("Unexpected error during login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error during login.");
        }
    }
}
