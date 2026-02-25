package com.online.ofos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private User customer;
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, 
    		orphanRemoval = true)
    @JsonManagedReference
    private List<CartItem> cartItems;
    private double totalAmount;
    

}
