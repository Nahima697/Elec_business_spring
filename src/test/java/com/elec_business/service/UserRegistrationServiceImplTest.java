package com.elec_business.service.impl;

import com.elec_business.controller.dto.RegistrationDto;
import com.elec_business.entity.User;
import com.elec_business.entity.UserRole;
import com.elec_business.repository.UserRepository;
import com.elec_business.repository.UserRoleRepository;
import com.elec_business.service.EmailVerificationService;
import com.elec_business.service.exception.EmailNotSentException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailVerificationService emailVerificationService;

    @InjectMocks
    private UserRegistrationServiceImpl userRegistrationService;

    // 1. Cas : Inscription réussie
    @Test
    void registerUser_Success() {
        // ARRANGE
        RegistrationDto dto = new RegistrationDto();
        dto.setEmail("newuser@test.com");
        dto.setUsername("newUser");
        dto.setPassword("plainPassword");
        dto.setPhoneNumber("0612345678");

        UserRole userRole = new UserRole();
        userRole.setName("USER");

        // On mocke toutes les étapes
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");
        when(userRoleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(emailVerificationService).sendVerificationEmail(any(User.class));

        // ACT
        User result = userRegistrationService.registerUser(dto);

        // ASSERT
        assertNotNull(result);
        assertEquals("newuser@test.com", result.getEmail());
        assertEquals("encodedPassword", result.getPassword()); 
        assertTrue(result.getRoles().contains(userRole)); 
        
        // Vérifie que l'email a bien été envoyé
        verify(emailVerificationService).sendVerificationEmail(any(User.class));
    }

    // 2. Cas d'erreur : Email déjà pris
    @Test
    void registerUser_Fail_EmailAlreadyExists() {
        // ARRANGE
        RegistrationDto dto = new RegistrationDto();
        dto.setEmail("existing@test.com");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        // ACT & ASSERT
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> 
            userRegistrationService.registerUser(dto)
        );
        assertEquals("Email already in use", ex.getMessage());

        verify(userRepository, never()).save(any());
    }

    // 3. Cas d'erreur : Rôle "USER" introuvable en base (Configuration BDD manquante)
    @Test
    void registerUser_Fail_RoleNotFound() {
        // ARRANGE
        RegistrationDto dto = new RegistrationDto();
        dto.setEmail("test@test.com");
        dto.setPassword("pwd");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRoleRepository.findByName("USER")).thenReturn(Optional.empty()); 

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class, () -> 
            userRegistrationService.registerUser(dto)
        );
        assertEquals("Error: Role is not found.", ex.getMessage());
    }

    // 4. Cas d'erreur : Echec de l'envoi d'email 
    @Test
    void registerUser_Fail_EmailSendingException() {
        // ARRANGE
        RegistrationDto dto = new RegistrationDto();
        dto.setEmail("test@test.com");
        dto.setPassword("pwd");

        UserRole userRole = new UserRole();
        when(userRepository.existsByEmail(false)).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRoleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        // On simule une erreur lors de l'envoi d'email
        doThrow(new RuntimeException("SMTP Error")).when(emailVerificationService).sendVerificationEmail(any(User.class));

        // ACT & ASSERT
        EmailNotSentException ex = assertThrows(EmailNotSentException.class, () -> 
            userRegistrationService.registerUser(dto)
        );
        assertTrue(ex.getMessage().contains("Failed to send verification email"));
    }
}
