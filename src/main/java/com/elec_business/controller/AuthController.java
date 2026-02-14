package com.elec_business.controller;

import com.elec_business.controller.dto.*;
import com.elec_business.entity.User;
import com.elec_business.controller.mapper.UserMapper;
import com.elec_business.repository.UserRepository;
import com.elec_business.service.AuthService;
import com.elec_business.service.EmailVerificationService;
import com.elec_business.service.ResetPasswordService;
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

import java.util.Map;

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
    private final ResetPasswordService resetPasswordService;

    @Value("${app.auth.frontend.url}")
    private String FRONT_URL;

    @Value("${application.api.url}")
    private String BACKEND_URL;

    // ---------------- REGISTER ----------------

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponseDto> register(
            @Valid @RequestBody RegistrationDto registrationDto) {

        User registeredUser =
                userRegistrationService.registerUser(
                        userMapper.toEntity(registrationDto)
                );

        emailVerificationService.sendVerificationToken(
                registeredUser.getId(),
                registeredUser.getEmail(),
                BACKEND_URL
        );

        RegistrationResponseDto responseDto =
                userMapper.toRegistrationResponseDto(
                        userMapper.toUserDto(registeredUser),
                        "Votre compte a été créé avec succès, vérifiez votre email"
                );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // ---------------- VERIFY EMAIL ----------------

    @GetMapping("/email/verify")
    public ResponseEntity<Void> verifyEmail(
            @RequestParam String userId,
            @RequestParam("t") String token) {

        emailVerificationService.verifyEmail(userId, token);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, FRONT_URL + "/login")
                .build();
    }

    @PostMapping("/email/resend")
    public ResponseEntity<String> resendEmailVerification(
            @RequestBody Map<String, String> payload) {

        String email = payload.get("email");

        appUserRepository.findByEmail(email)
                .ifPresent(user ->
                        emailVerificationService.sendVerificationToken(
                                user.getId(),
                                user.getEmail(),
                                BACKEND_URL
                        )
                );

        // Toujours OK pour éviter enumeration
        return ResponseEntity.ok(
                "If the account exists, a verification email has been sent."
        );
    }

    // ---------------- LOGIN ----------------

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginCredentialsDTO loginDto) {

        try {

            User user = authService.authenticateUser(
                    loginDto.getUsername(),
                    loginDto.getPassword()
            );

            String jwt = authService.generateJwtToken(user);
            String refreshToken = authService.generateRefreshToken(user);

            ResponseCookie refreshCookie =
                    authService.createRefreshTokenCookie(refreshToken);

            LoginResponseDTO responseDto =
                    new LoginResponseDTO( userMapper.toUserDto(user));

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(responseDto);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // ---------------- CURRENT USER ----------------

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getCurrentUser(
            @AuthenticationPrincipal User currentUser) {

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(
                userMapper.toUserProfileDto(currentUser)
        );
    }

    // ---------------- REFRESH TOKEN ----------------

    @PostMapping("/refresh-token")
    public ResponseEntity<Void> refreshToken(
            @CookieValue(name = "refresh-token", required = false) String token
    ) {
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing refresh token");
        }

        try {
            TokenPair tokens = authService.validateRefreshToken(token);

            ResponseCookie refreshCookie = authService.createRefreshTokenCookie(tokens.getRefreshToken());
            ResponseCookie accessCookie  = authService.createAccessTokenCookie(tokens.getJwt());

            return ResponseEntity.noContent()
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                    .build();

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid refresh token");
        }
    }

    // ---------------- RESET PASSWORD REQUEST ----------------

    @PostMapping("/reset-password")
    public ResponseEntity<String> sendResetPassword(
            @RequestBody Map<String, String> payload) {

        String email = payload.get("email");

        appUserRepository.findByEmail(email)
                .ifPresent(user ->
                        resetPasswordService
                                .sendPasswordResetToken(
                                        user.getId(),
                                        user.getEmail(),
                                        BACKEND_URL
                                )
                );

        return ResponseEntity.ok(
                "If the account exists, a reset link has been sent."
        );
    }

    // ---------------- RESET PASSWORD CONFIRM ----------------

    @PostMapping("/reset-password/confirm")
    public ResponseEntity<String> confirmResetPassword(
            @RequestBody ResetPasswordConfirmDTO dto) {

        resetPasswordService.resetPassword(
                dto.getUserId(),
                dto.getToken(),
                dto.getNewPassword()
        );

        return ResponseEntity.ok("Password successfully reset.");
    }

}


