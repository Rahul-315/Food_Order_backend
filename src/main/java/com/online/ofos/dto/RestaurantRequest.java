package com.online.ofos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RestaurantRequest {

    @NotBlank(message = "Restaurant name is required")
    @Size(min = 3, max = 100, message = "Restaurant name must be between 3 and 100 characters")
    private String name;

    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 255, message = "Address must be between 5 and 255 characters")
    private String address;

    @Size(max = 500, message = "Description can be up to 500 characters")
    private String description;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Contact email is required")
    private String contactEmail;

    @NotBlank(message = "Contact phone is required")
    @Pattern(regexp = "^[7-9][0-9]{9}$", message = "Phone number must start with 7, 8, or 9 and be 10 digits")
    private String contactPhone;

    @Pattern(
        regexp = "^(https?://.*\\.(?:png|jpg|jpeg|gif|webp))?$",
        message = "Image URL must be a valid URL ending with png, jpg, jpeg, gif, or webp"
    )
    private String imageUrl;
}
