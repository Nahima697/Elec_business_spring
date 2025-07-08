package com.elec_business.service;

import com.elec_business.model.AppUser;
import com.elec_business.exception.EmailNotVerifiedException;
import com.elec_business.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final AppUserRepository appUserRepository;

    @Value("${email-verification.required:true}")
    private boolean emailVerificationRequired;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        AppUser user = appUserRepository.findByIdentifier(identifier).orElseThrow(() ->
                new UsernameNotFoundException("User not found with identifier: " + identifier)
        );

        if (emailVerificationRequired && Boolean.FALSE.equals(user.getEmailVerified())) {
            throw new EmailNotVerifiedException("Email not verified");
        }

        return user;
    }
}
