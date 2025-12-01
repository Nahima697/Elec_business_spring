package com.elec_business.business.impl;

import com.elec_business.business.UserRoleBusiness;
import com.elec_business.entity.User;
import com.elec_business.entity.UserRole;
import com.elec_business.repository.UserRepository;
import com.elec_business.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRoleBusinessImpl implements UserRoleBusiness {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public void addRoleOwner(String id) {
        addRole(id, "ROLE_OWNER");
    }

    @Override
    public void addRoleRenter(String id) {
        addRole(id, "ROLE_RENTER");
    }

    private void addRole(String userId, String roleName) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User introuvable"));

        UserRole role = userRoleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role introuvable : " + roleName));

        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    public void removeRole(String userId, String roleName) {
        User user = userRepository.findById(userId).orElseThrow();
        UserRole role = userRoleRepository.findByName(roleName).orElseThrow();
        user.getRoles().remove(role);
        userRepository.save(user);
    }
}
