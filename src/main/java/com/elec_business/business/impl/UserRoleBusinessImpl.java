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

        //Verifier que l'utilisateur existe
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User introuvable"));
        //Recuperer le role Owner
        UserRole role = userRoleRepository.findByName("Owner");
        user.setRole(role);
    }

    @Override
    public void addRoleRenter(String id) {

        //Verifier que l'utilisateur existe
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User introuvable"));
        //Recuperer le role Renter
        UserRole role = userRoleRepository.findByName("Renter");
        user.setRole(role);
    }
}
