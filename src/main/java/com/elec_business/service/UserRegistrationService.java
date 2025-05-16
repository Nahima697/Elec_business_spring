package com.elec_business.service;

import com.elec_business.dto.RegistrationDto;
import com.elec_business.entity.AppUser;
import com.elec_business.mapper.UserRegistrationMapper;
import com.elec_business.repository.AppUserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRegistrationMapper userRegistrationMapper;

    @Transactional
    public AppUser registerUser(@Valid RegistrationDto request) {
        if (userRepository.existsByUsername(request.getUsername()) ||
                userRepository.existsByEmail(request.getEmail())) {

            throw new ValidationException(
                    "Username or Email already exists");
        }

        AppUser user = userRegistrationMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmailVerified(false);

        return userRepository.save(user);
    }
}