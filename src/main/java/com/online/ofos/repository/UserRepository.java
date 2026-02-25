package com.online.ofos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.online.ofos.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
    Optional<User> findByMobileNumber(String mobileNumber);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
    boolean existsByMobileNumber(String mobileNumber);
   
}
