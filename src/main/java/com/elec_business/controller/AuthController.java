package com.elec_business.controller;

import com.elec_business.controller.dto.*;
import com.elec_business.security.jwt.JwtUtil;
import com.elec_business.entity.User;
import com.elec_business.security.exception.EmailNotVerifiedException;
import com.elec_business.controller.mapper.UserMapper;
import com.elec_business.repository.UserRepository;
import com.elec_business.service.AuthService;
import com.elec_business.service.EmailVerificationService;
import com.elec_business.service.UserRegistrationService;
import com.elec_business.service.impl.TokenPair;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
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

    @Value("${app.auth.email-verification-required:true}")
    private boolean emailVerificationRequired;

    @Value("${app.auth.frontend.url}")
    private String FRONT_URL;

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponseDto> register(@RequestBody @Valid RegistrationDto registrationDto) {
        try {
            // 1. Création de l'utilisateur
            User registeredUser = userRegistrationService.registerUser(userMapper.toEntity(registrationDto));

            // 2. Envoi de l'email
            emailVerificationRequired = true;
                emailVerificationService.sendVerificationToken(
                        registeredUser.getId(),
                        registeredUser.getEmail()
                );

            // 3. Création de la réponse succès
            RegistrationResponseDto responseDto = userMapper.toRegistrationResponseDto(registeredUser,emailVerificationRequired,
                    "Votre compte a été créé avec succès vérifier votre email");

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);

        } catch (ValidationException ve) {
            // Création de la réponse erreur spécifique
            RegistrationResponseDto responseDto = new RegistrationResponseDto(
                    null,
                    false,
                    ve.getMessage()
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseDto);

        } catch (Exception e) {
            log.error("Error during registration", e);

            // Création de la réponse erreur générique
            RegistrationResponseDto responseDto = new RegistrationResponseDto(
                    null,
                    false,
                    "Une erreur est survenue lors de la création du compte."
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }


    @GetMapping("/email/verify")
    public ResponseEntity<Void> verifyEmail(
            @RequestParam String userId, @RequestParam("t") String token) {
        final var verifiedUser =
                emailVerificationService.verifyEmail(userId, token);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", FRONT_URL + "/email-verified?success=true")
                .build();
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
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginCredentialsDTO loginDto) {
        try {
            // Authentifie l'utilisateur via le service
            User user = authService.authenticateUser(loginDto.getUsername(), loginDto.getPassword());

            // Génère le JWT
            String jwt = authService.generateJwtToken(user);

            // Génère le refresh token cookie
            String refreshToken = authService.generateRefreshToken(user.getId());
            ResponseCookie refreshCookie= authService.createRefreshTokenCookie(refreshToken);
            // Crée la réponse DTO
            LoginResponseDTO responseDto = new LoginResponseDTO(jwt, userMapper.toDTO(user));

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(responseDto);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/me")
        public ResponseEntity<UserRegisterDto> getCurrentUser (@AuthenticationPrincipal User currentUser){
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
              return ResponseEntity.ok(userMapper.toDto(currentUser));
        }

    @PostMapping("/api/refresh-token")
    public ResponseEntity<String> refreshToken(@CookieValue(name = "refresh-token") String token) {
        try {

            TokenPair tokens = authService.validateRefreshToken(token);
            ResponseCookie refreshCookie = generateCookie(tokens.getRefreshToken());
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(tokens.getJwt());

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid refresh token");
        }

    }

    @GetMapping("/api/protected")
    public String protec(@AuthenticationPrincipal User user) {
        System.out.println("hola");
        return user.getEmail();
    }

    private ResponseCookie generateCookie(String refreshToken) {
       return ResponseCookie.from("refresh-token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite(SameSiteCookies.NONE.toString())
                .path("/api/refresh-token")
                .build()
                ;
    }
}


