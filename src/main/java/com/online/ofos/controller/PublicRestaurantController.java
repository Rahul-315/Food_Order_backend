package com.online.ofos.controller;

import com.online.ofos.entity.MenuItem;
import com.online.ofos.entity.Restaurant;
import com.online.ofos.service.MenuService;
import com.online.ofos.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api") // public API
public class PublicRestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private MenuService menuService;

    // GET /api/restaurants → all restaurants
    @GetMapping("/restaurants")
    public ResponseEntity<?> getAllRestaurants() {
        try {
            List<Restaurant> restaurants = restaurantService.getAllRestaurants();
            return ResponseEntity.ok(restaurants);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch restaurants: " + e.getMessage());
        }
    }

    // GET /api/menu?restaurantId=1 → menu items for a specific restaurant
    @GetMapping("/menu")
    public ResponseEntity<?> getMenuByRestaurant(@RequestParam Long restaurantId) {
        try {
            // ✅ Fetch the Restaurant object first
            Restaurant restaurant = restaurantService.getRestaurant(restaurantId);

            // ✅ Then fetch menu items using MenuService
            List<MenuItem> menuItems = menuService.getMenuByRestaurant(restaurant);

            return ResponseEntity.ok(menuItems);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch menu: " + e.getMessage());
        }
    }

    // GET /api/restaurants/{id} → single restaurant by ID
    @GetMapping("/restaurants/{id}")
    public ResponseEntity<?> getRestaurantById(@PathVariable Long id) {
        try {
            Restaurant restaurant = restaurantService.getRestaurant(id);
            return ResponseEntity.ok(restaurant);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch restaurant: " + e.getMessage());
        }
    }
}
