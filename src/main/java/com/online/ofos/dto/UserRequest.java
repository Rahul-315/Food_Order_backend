package com.online.ofos.dto;

import com.online.ofos.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {

    @NotBlank(message = "Username must not be empty")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(
        regexp = "^(?!\\s+$).*$",
        message = "Username cannot be blank or whitespace only"
    )
    private String username;

    @NotBlank(message = "Email must not be empty")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password must not be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
        message = "Password must contain at least 1 uppercase letter, 1 number, and 1 special character"
    )
    private String password;

    @NotBlank(message = "Confirm password must not be empty")
    private String confirmPassword;

    @NotNull(message = "Role must be specified")
    private Role role;

    private Long restaurantId;

    @NotBlank(message = "Mobile number is required")
    @Pattern(
        regexp = "^[7-9][0-9]{9}$",
        message = "Mobile number must start with 7, 8, or 9 and be 10 digits long"
    )
    private String mobileNumber;
    @NotBlank(message = "Address is required")
    @Size(min = 10, max = 255, message = "Address must be between 10 and 255 characters")
    private String address;
}
