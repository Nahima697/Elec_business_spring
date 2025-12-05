package com.elec_business.service.impl;

import com.elec_business.entity.User;
import com.elec_business.entity.UserRole;
import com.elec_business.repository.UserRepository;
import com.elec_business.service.UserRegistrationService;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class UserRegistrationServiceImpl implements  UserRegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername()) ||
                userRepository.existsByEmail(user.getEmail())) {
            throw new ValidationException(
                    "Username or Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEmailVerified(false);
        // Ajout du rôle "USER" par défaut (ID 1)
        UserRole defaultRole = new UserRole();
        defaultRole.setId(1);

        // Initialiser la liste des rôles si nécessaire
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }

        user.getRoles().add(defaultRole);

        return userRepository.save(user);
    }
}