package com.online.ofos.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Restaurant restaurant;

    private String name;
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private FoodType foodType;

    @Enumerated(EnumType.STRING)
    private FoodCategory category;

    private double price;

    @Column(nullable = true)
    private Double rating;

    private boolean available;
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
}