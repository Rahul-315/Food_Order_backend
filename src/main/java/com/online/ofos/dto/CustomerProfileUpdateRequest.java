package com.online.ofos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerProfileUpdateRequest {

    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank(message = "Mobile number is required")
    @Pattern(
        regexp = "^[7-9][0-9]{9}$",
        message = "Mobile number must start with 7, 8, or 9 and be 10 digits long"
    )
    private String mobileNumber;

    // 🔹 OPTIONAL password change (fixed to allow empty)
    @Pattern(
        regexp = "^$|^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
        message = "Password must be strong"
    )
    private String password;
    @NotBlank(message = "Address is required")
    @Size(min = 10, max = 255, message = "Address must be between 10 and 255 characters")
    private String address;


    private String confirmPassword;
}
