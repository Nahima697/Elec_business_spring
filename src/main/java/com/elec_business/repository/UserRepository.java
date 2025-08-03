package com.elec_business.repository;

import com.elec_business.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    @Query("FROM User u WHERE u.email=:identifier OR u.username=:identifier")
    Optional<User> findByIdentifier(String identifier);
}
