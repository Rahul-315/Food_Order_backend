package com.online.ofos.dto;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PartnerRestaurantRequestDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String address;

    private String description;

    @Email
    @NotBlank
    private String contactEmail;

    private String contactPhone;

    private String imageUrl;
   
}
