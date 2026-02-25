package com.online.ofos.config;

import com.online.ofos.entity.User;
import com.online.ofos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminPasswordReset implements CommandLineRunner {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        User admin = userRepo.findByUsername("admin").orElse(null);
        if (admin != null) {
            admin.setPassword(passwordEncoder.encode("Admin@123")); 
            userRepo.save(admin);
            System.out.println("Admin password reset to 'Admin@123'");
        }
    }
}
