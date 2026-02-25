package com.online.ofos.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.online.ofos.entity.PasswordResetOtp;
import com.online.ofos.entity.User;
import com.online.ofos.repository.PasswordResetOtpRepository;
import com.online.ofos.repository.UserRepository;
import com.online.ofos.exception.ResourceNotFoundException;
import com.online.ofos.exception.BadRequestException;



import jakarta.transaction.Transactional;
@Service
@Transactional
public class ForgotPasswordService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordResetOtpRepository otpRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void sendOtp(String email) {

        userRepo.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Email not registered"));

        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        otpRepo.deleteByEmail(email);

        PasswordResetOtp resetOtp = PasswordResetOtp.builder()
                .email(email)
                .otp(otp)
                .expiryTime(LocalDateTime.now().plusMinutes(3))
                .build();

        otpRepo.save(resetOtp);
        emailService.sendPasswordResetOtp(email, otp);
    }

    public void verifyOtp(String email, String otp) {

        PasswordResetOtp savedOtp = otpRepo
            .findByEmailAndOtp(email, otp)
            .orElseThrow(() -> new BadRequestException("Invalid OTP"));

        if (savedOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP expired");
        }
    }

    public void resetPassword(String email, String otp, String newPassword) {

        verifyOtp(email, otp);

        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        otpRepo.deleteByEmail(email);
    }
}
