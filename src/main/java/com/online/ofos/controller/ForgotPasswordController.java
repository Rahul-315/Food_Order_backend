package com.online.ofos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.online.ofos.dto.*;
import com.online.ofos.service.ForgotPasswordService;

@RestController
@RequestMapping("/api/auth/password")
public class ForgotPasswordController {

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @PostMapping("/forgot")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest req) {
        forgotPasswordService.sendOtp(req.getEmail());
        return ResponseEntity.ok("OTP sent to email");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpRequest req) {
        forgotPasswordService.verifyOtp(req.getEmail(), req.getOtp());
        return ResponseEntity.ok("OTP verified");
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest req) {
        forgotPasswordService.resetPassword(
                req.getEmail(),
                req.getOtp(),
                req.getNewPassword()
        );
        return ResponseEntity.ok("Password reset successful");
    }
}
