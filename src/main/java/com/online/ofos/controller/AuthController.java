package com.online.ofos.controller;
import com.online.ofos.config.JwtUtil;
import com.online.ofos.dto.LoginRequest;
import com.online.ofos.dto.RegisterRequest;
import com.online.ofos.entity.User;
import com.online.ofos.exception.BadRequestException;
import com.online.ofos.repository.UserRepository;
import com.online.ofos.service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private EmailService emailService;
    @Autowired
    private com.online.ofos.service.GoogleTokenService googleTokenService;


    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest req) {

        User user = userRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Password is wrong");
        }
        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole().name()
        );

        return ResponseEntity.ok(token);
    }
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest req) {

        if (userRepo.findByUsername(req.getUsername()).isPresent()) {
            throw new BadRequestException("Username already exists");
        }

        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new BadRequestException("Email already registered");
        }
        if (userRepo.existsByMobileNumber(req.getMobileNumber())) {
            throw new BadRequestException("Mobile number already registered");
        }

        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .mobileNumber(req.getMobileNumber())
                .address(req.getAddress())
                .role(com.online.ofos.entity.Role.CUSTOMER)
                .build();

        User savedUser = userRepo.save(user);

        
        if (savedUser.getEmail() != null && !savedUser.getEmail().isBlank()) {
            emailService.sendWelcomeMail(
                    savedUser.getEmail(),
                    savedUser.getUsername()
            );
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedUser);
    }
    
    @PostMapping("/google")
    public ResponseEntity<String> googleLogin(@RequestBody com.online.ofos.dto.GoogleLoginRequest req) {

        var payload = googleTokenService.verifyToken(req.getIdToken());

        if (payload == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid Google token");
        }

        String email = payload.getEmail();
        String name = (String) payload.get("name");

        // Check if user exists, else create new user
        User user = userRepo.findByEmail(email).orElseGet(() -> {
            // Generate a username based on email
            String username = email.split("@")[0];

            // Ensure username is unique
            int counter = 1;
            while (userRepo.findByUsername(username).isPresent()) {
                username = username + counter;
                counter++;
            }

            User newUser = User.builder()
                    .username(username)
                    .email(email)
                    .password(passwordEncoder.encode("GOOGLE_LOGIN"))
                    .role(com.online.ofos.entity.Role.CUSTOMER)
                    .address("Not provided")       // ✅ Required field
                    .mobileNumber("0000000000")    // ✅ Provide default
                    .build();

            return userRepo.save(newUser);
        });

        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole().name()
        );

        return ResponseEntity.ok(token);
    }

}