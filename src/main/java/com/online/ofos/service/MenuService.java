package com.online.ofos.service;

import com.online.ofos.dto.MenuItemRequest;
import com.online.ofos.entity.FoodType;
import com.online.ofos.entity.MenuItem;
import com.online.ofos.entity.Restaurant;
import com.online.ofos.exception.ResourceNotFoundException;
import com.online.ofos.repository.MenuItemRepository;
import com.online.ofos.repository.RestaurantRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MenuService {

    @Autowired
    private MenuItemRepository menuRepo;

    @Autowired
    private RestaurantRepository restaurantRepo;
    @Transactional
    public MenuItem addMenuItem(Long restaurantId, MenuItemRequest req) {
        Restaurant r = restaurantRepo.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        MenuItem m = MenuItem.builder()
                .restaurant(r)
                .name(req.getName())
                .description(req.getDescription())
                .category(req.getCategory())
                .foodType(req.getFoodType() != null ? req.getFoodType() : FoodType.VEG)
                .price(req.getPrice())
                .available(req.isAvailable())
                .imageUrl(req.getImageUrl())
                .rating(req.getRating())
                .build();
                
        return menuRepo.save(m);
    }
    @Transactional
    public Restaurant getRestaurantById(Long restaurantId) {
        return restaurantRepo.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
    }
    @Transactional
    public List<MenuItem> getMenuByRestaurant(Restaurant r) {
        return menuRepo.findByRestaurant(r);
    }
    @Transactional
    public MenuItem getMenuItem(Long id) {
        return menuRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));
    }
    @Transactional
    public MenuItem updateMenuItem(Long id, MenuItemRequest req) {
        MenuItem m = getMenuItem(id);
        m.setName(req.getName());
        m.setDescription(req.getDescription());
        m.setCategory(req.getCategory());
        m.setPrice(req.getPrice());
        m.setAvailable(req.isAvailable());
        m.setImageUrl(req.getImageUrl());
        if (req.getFoodType() != null) {
            m.setFoodType(req.getFoodType());
        }
        m.setRating(req.getRating());
        return menuRepo.save(m);
    }
    @Transactional
    public void deleteMenuItem(Long id) {
        menuRepo.delete(getMenuItem(id));
    }
}