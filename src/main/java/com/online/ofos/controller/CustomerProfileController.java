package com.online.ofos.controller;

import com.online.ofos.dto.CustomerProfileUpdateRequest;
import com.online.ofos.entity.User;
import com.online.ofos.repository.UserRepository;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer/profile")
public class CustomerProfileController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 🔹 Get logged-in customer profile
    @GetMapping
    public ResponseEntity<User> getProfile(Authentication auth) {
        User user = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }

    // 🔹 Update logged-in customer profile
    @PutMapping
    public ResponseEntity<User> updateProfile(
            Authentication auth,
            @Valid @RequestBody CustomerProfileUpdateRequest req) {

        User user = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔐 Password check only if user entered it
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            if (!req.getPassword().equals(req.getConfirmPassword())) {
                throw new RuntimeException("Passwords do not match");
            }
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }

        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setMobileNumber(req.getMobileNumber());
        user.setAddress(req.getAddress()); 

        return ResponseEntity.ok(userRepo.save(user));
    }

}
