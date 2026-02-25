package com.online.ofos.dto;

import com.online.ofos.entity.FoodCategory;
import com.online.ofos.entity.FoodType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MenuItemRequest {

    @NotBlank(message = "Menu item name must not be empty")
    @Size(min = 2, max = 50, message = "Menu item name must be between 2 and 50 characters")
    private String name;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @NotNull(message = "Food category must be selected")
    private FoodCategory category;

    @DecimalMin(value = "0.1", inclusive = true, message = "Price must be greater than 0")
    private double price;
    private FoodType foodType;

    private boolean available;
    
    private String imageUrl;

    @DecimalMin(value = "0.0", inclusive = true, message = "Rating must be >= 0")
    private Double rating; 


}
