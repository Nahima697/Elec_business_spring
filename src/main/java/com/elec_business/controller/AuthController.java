package com.elec_business.controller;

import com.elec_business.controller.dto.*;
import com.elec_business.entity.User;
import com.elec_business.controller.mapper.UserMapper;
import com.elec_business.repository.UserRepository;
import com.elec_business.service.AuthService;
import com.elec_business.service.EmailVerificationService;
import com.elec_business.service.UserRegistrationService;
import com.elec_business.service.impl.TokenPair;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final EmailVerificationService emailVerificationService;
    private final AuthService authService;
    private final UserRegistrationService userRegistrationService;
    private final UserRepository appUserRepository;
    private final UserMapper userMapper;

    @Value("${app.auth.frontend.url}")
    private String FRONT_URL;

    @Value("${application.api.url}")
    private String BACKEND_URL;

    // --- REGISTER ---
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponseDto> register(@RequestBody @Valid RegistrationDto registrationDto) {
        User registeredUser = userRegistrationService.registerUser(
                userMapper.toEntity(registrationDto)
        );

        emailVerificationService.sendVerificationToken(
                registeredUser.getId(),
                registeredUser.getEmail(),
                BACKEND_URL
        );

        RegistrationResponseDto responseDto = userMapper.toRegistrationResponseDto(
                userMapper.toUserDto(registeredUser),
                "Votre compte a été créé avec succès, vérifiez votre email"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // --- VERIFY EMAIL ---
    @GetMapping("/email/verify")
    public ResponseEntity<Void> verifyEmail(@RequestParam String userId, @RequestParam("t") String token) {
        emailVerificationService.verifyEmail(userId, token);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", FRONT_URL + "/login")
                .build();
    }

    @PostMapping("/email/resend")
    public ResponseEntity<String> resendEmailVerification(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        Optional<User> user = appUserRepository.findByEmail(email);

        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("Utilisateur invalide ou déjà vérifié.");
        }
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        emailVerificationService.sendVerificationToken(user.get().getId(), user.get().getEmail(), baseUrl);
        return ResponseEntity.ok("Email de vérification renvoyé.");
    }

    // --- LOGIN ---
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginCredentialsDTO loginDto) {
        try {
            // 1. Authentification
            User user = authService.authenticateUser(loginDto.getUsername(), loginDto.getPassword());

            // 2. Génération JWT
            String jwt = authService.generateJwtToken(user);

            // 3. Génération Refresh Token
            String refreshToken = authService.generateRefreshToken(user);

            // 4. Création Cookie HttpOnly
            ResponseCookie refreshCookie = authService.createRefreshTokenCookie(refreshToken);

            // 5. Réponse DTO + Cookie dans le header
            LoginResponseDTO responseDto = new LoginResponseDTO(jwt, userMapper.toUserDto(user),refreshToken);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(responseDto);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            log.error("Erreur Login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getCurrentUser(@AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(userMapper.toUserProfileDto(currentUser));
    }

    // Accepte le token soit par Cookie (Web), soit par Body (Mobile)
    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(
            @CookieValue(name = "refresh-token", required = false) String cookieToken,
            @RequestBody(required = false) Map<String, String> body) {

        try {
            // 1. Récupération  du token
            String tokenToVerify = cookieToken;
            if (tokenToVerify == null && body != null) {
                tokenToVerify = body.get("refreshToken");
            }

            if (tokenToVerify == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            // 2. Validation & Rotation via Service
            TokenPair tokens = authService.validateRefreshToken(tokenToVerify);

            // 3. Création du nouveau cookie
            ResponseCookie refreshCookie = authService.createRefreshTokenCookie(tokens.getRefreshToken());

            // 4. Réponse JSON
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(Collections.singletonMap("token", tokens.getJwt()));

        } catch (Exception e) {
            log.warn("Refresh failed: {}", e.getMessage());
            // Renvoie 403 Forbidden pour dire au front de se déconnecter
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid refresh token");
        }
    }

    @GetMapping("/protected")
    public String protec(@AuthenticationPrincipal User user) {
        return user.getEmail();
    }
}