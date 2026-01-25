package com.elec_business.business.impl;

import com.elec_business.business.UserProfilBusiness;
import com.elec_business.controller.dto.UserProfileDto;
import com.elec_business.controller.mapper.UserMapper;
import com.elec_business.entity.BillingAddress;
import com.elec_business.entity.User;
import com.elec_business.repository.BillingAddressRepository;
import com.elec_business.repository.UserRepository;
import com.elec_business.service.FileStorageService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserProfileBusinessImpl implements UserProfilBusiness {
    private final UserRepository userRepository;
    private final BillingAddressRepository billingAddressRepository;
    private final UserMapper userMapper;
    private final FileStorageService fileStorageService;
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

    @Override
    @Transactional
    public UserProfileDto uploadAvatar(String userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String fileUrl = fileStorageService.upload(file);

        user.setProfilePictureUrl(fileUrl);
        userRepository.save(user);

        BillingAddress address = billingAddressRepository.findByUserId(userId);

        return userMapper.toUserProfileWithDetailDto(user, address);
    }
}

