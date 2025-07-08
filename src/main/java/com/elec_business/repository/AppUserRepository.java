package com.elec_business.repository;

import com.elec_business.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    Optional<AppUser> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<AppUser> findByEmail(String email);
    boolean existsByEmail(String email);
    @Query("FROM AppUser u WHERE u.email=:identifier OR u.username=:identifier")
    Optional<AppUser> findByIdentifier(String identifier);
}
