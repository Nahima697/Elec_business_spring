package com.elec_business.repository;

import com.elec_business.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser,Long> {
    AppUser findByUsername(String username);
    boolean existsByUsername(String username);
}