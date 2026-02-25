package com.online.ofos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOtpRequest {
    private String email;

    @NotBlank
    private String otp;
}
