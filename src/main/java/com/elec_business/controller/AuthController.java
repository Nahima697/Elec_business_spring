package com.elec_business.controller;

import com.elec_business.security.jwt.JwtUtil;
import com.elec_business.controller.dto.RegistrationDto;
import com.elec_business.controller.dto.RegistrationResponseDto;
import com.elec_business.controller.dto.UserRegisterDto;
import com.elec_business.entity.User;
import com.elec_business.security.exception.EmailNotVerifiedException;
import com.elec_business.controller.mapper.UserMapper;
import com.elec_business.controller.mapper.RegistrationResponseMapper;
import com.elec_business.repository.UserRepository;
import com.elec_business.service.impl.EmailVerificationServiceImpl;
import com.elec_business.service.impl.UserRegistrationServiceImpl;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailVerificationServiceImpl emailVerificationService;
    private final UserMapper userMapper;
    private final UserRegistrationServiceImpl userRegistrationService;
    private final RegistrationResponseMapper registrationResponseMapper;
    private final UserRepository appUserRepository;

    @Value("${app.auth.email-verification-required:true}")
    private boolean emailVerificationRequired;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegistrationDto registrationDto) {
        User registeredUser;

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
            @RequestParam String userId, @RequestParam("t") String token) {
        final var verifiedUser =
                emailVerificationService.verifyEmail(userId, token);

        return ResponseEntity.ok(userMapper.toDto(verifiedUser));
    }

    @PostMapping("/email/resend")
    public ResponseEntity<String> resendEmailVerification(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        Optional<User> user = appUserRepository.findByEmail(email);

        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid or already verified user.");
        }

        emailVerificationService.sendVerificationToken(user.get().getId(), user.get().getEmail());
        return ResponseEntity.ok("Verification email resent.");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
        if (user.getUsername() == null || user.getPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing username or password."));
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            if (authentication.isAuthenticated()) {
                Map<String, Object> authData = new HashMap<>();
                authData.put("token", jwtUtil.generateToken(user.getUsername()));
                authData.put("type", "Bearer");

                return ResponseEntity.ok(authData);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Authentication failed."));
            }

        } catch (AuthenticationException e) {
            Throwable cause = e.getCause();
            if (cause instanceof EmailNotVerifiedException) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Email not verified."));
            }

            log.warn("Authentication failed for user: {}", user.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password."));
        } catch (Exception e) {
            log.error("Unexpected error during login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected error during login."));
        }
    }
        @GetMapping("/me")
        public ResponseEntity<UserRegisterDto> getCurrentUser (@AuthenticationPrincipal User currentUser){
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
              return ResponseEntity.ok(userRegistrationService.getCurrentUser(currentUser));
        }
}


