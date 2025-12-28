package com.elec_business.business.impl;

import com.elec_business.business.UserProfilBusiness;
import com.elec_business.controller.dto.UserProfileDto;
import com.elec_business.controller.mapper.UserMapper;
import com.elec_business.entity.BillingAddress;
import com.elec_business.entity.User;
import com.elec_business.repository.BillingAddressRepository;
import com.elec_business.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileBusinessImpl implements UserProfilBusiness {
    private final UserRepository userRepository;
    private final BillingAddressRepository billingAddressRepository;
    private final UserMapper userMapper;
    @Override
    @Transactional
    public UserProfileDto getProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));

        // On cherche l'adresse associée
        BillingAddress address = billingAddressRepository.findByUserId(userId);

        return userMapper.toUserProfileWithDetailDto(user, address);
    }

    @Override
    @Transactional
    public UserProfileDto updateProfile(String userId, UserProfileDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Mise à jour User
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        userRepository.save(user);

        // Mise à jour ou Création Adresse
        BillingAddress address =billingAddressRepository.findByUserId(userId);

        if (address == null) {
            address = new BillingAddress();
            address.setUser(user);
        }

        address.setAddressLine(dto.getAddressLine());
        address.setCity(dto.getCity());
        address.setPostalCode(dto.getPostalCode());
        address.setCountry(dto.getCountry());

        billingAddressRepository.save(address);

        return userMapper.toUserProfileWithDetailDto(user, address);
    }

    private UserProfileDto mapToDto(User user, BillingAddress address) {
        // 1. On mappe les infos de base du User via le mapper
        UserProfileDto dto = userMapper.toUserProfileDto(user);

        // 2. On ajoute les infos de l'adresse manuellement si elle existe
        if (address != null) {
            dto.setAddressLine(address.getAddressLine());
            dto.setCity(address.getCity());
            dto.setPostalCode(address.getPostalCode());
            dto.setCountry(address.getCountry());
        }
        return dto;
    }
}
