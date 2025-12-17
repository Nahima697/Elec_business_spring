package com.elec_business.service.impl;

import com.elec_business.entity.User;
import com.elec_business.entity.UserRole;
import com.elec_business.repository.UserRepository;
import com.elec_business.service.EmailVerificationService; 
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private EmailVerificationService emailVerificationService; 

    @InjectMocks
    private UserRegistrationServiceImpl userRegistrationService;

    // 1. Cas nominal : Inscription réussie
    @Test
    void registerUser_Success() {
        // ARRANGE
        User user = new User();
        user.setEmail("newuser@test.com");
        user.setUsername("newUser");
        user.setPassword("plainPassword");
     

        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        
        // On retourne l'utilisateur sauvegardé
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        User result = userRegistrationService.registerUser(user);

        // ASSERT
        assertNotNull(result);
        assertEquals("encodedPassword", result.getPassword()); 
        assertFalse(result.isEmailVerified()); 
        
        // Vérifie l'ajout du rôle par défaut (ID 1)
        assertNotNull(result.getRoles());
        assertTrue(result.getRoles().stream().anyMatch(r -> r.getId() == 1));

    }

    // 2. Cas d'erreur : Email ou Username déjà pris
    @Test
    void registerUser_Fail_AlreadyExists() {
        // ARRANGE
        User user = new User();
        user.setUsername("existingUser");
        user.setEmail("test@test.com");
        user.setPassword("pwd");

        when(userRepository.existsByUsername("existingUser")).thenReturn(true);
  
        // ACT & ASSERT
        ValidationException ex = assertThrows(ValidationException.class, () -> 
            userRegistrationService.registerUser(user)
        );
        assertEquals("Username or Email already exists", ex.getMessage());

        verify(userRepository, never()).save(any());
    }
}
