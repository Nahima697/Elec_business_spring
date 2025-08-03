package com.elec_business.security;

import com.elec_business.entity.User;
import com.elec_business.security.exception.EmailNotVerifiedException;
import com.elec_business.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Value("${email-verification.required:true}")
    private boolean emailVerificationRequired;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = userRepository.findByIdentifier(identifier).orElseThrow(() ->
                new UsernameNotFoundException("User not found with identifier: " + identifier)
        );

        if (emailVerificationRequired && Boolean.FALSE.equals(user.getEmailVerified())) {
            throw new EmailNotVerifiedException();
        }

        return user;
    }
}
