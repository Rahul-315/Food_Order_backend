package com.online.ofos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.online.ofos.dto.RestaurantRequest;
import com.online.ofos.entity.Restaurant;
import com.online.ofos.exception.ResourceNotFoundException;
import com.online.ofos.repository.RestaurantRepository;

import jakarta.transaction.Transactional;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepo;

    @Autowired
    private EmailService emailService; 

    @Transactional
    public Restaurant addRestaurant(RestaurantRequest req) {

        Restaurant r = Restaurant.builder()
                .name(req.getName())
                .address(req.getAddress())
                .description(req.getDescription())
                .contactEmail(req.getContactEmail())
                .contactPhone(req.getContactPhone())
                .imageUrl(req.getImageUrl())
                .build();

        Restaurant savedRestaurant = restaurantRepo.save(r);

        
        emailService.sendRestaurantAddedMail(
                savedRestaurant.getContactEmail(),
                savedRestaurant.getName()
        );

        return savedRestaurant;
    }

    @Transactional
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepo.findAll();
    }

    public Restaurant getRestaurant(Long id) {
        return restaurantRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Restaurant not found with id: " + id));
    }

    @Transactional
    public Restaurant updateRestaurant(Long id, RestaurantRequest req) {

        Restaurant r = getRestaurant(id);

        r.setName(req.getName());
        r.setAddress(req.getAddress());
        r.setDescription(req.getDescription());
        r.setContactEmail(req.getContactEmail());
        r.setContactPhone(req.getContactPhone());
        r.setImageUrl(req.getImageUrl());

        return r;
    }

    @Transactional
    public void deleteRestaurant(Long id) {

        Restaurant r = getRestaurant(id);

       
        emailService.sendRestaurantDeletedMail(
                r.getContactEmail(),
                r.getName()
        );

        restaurantRepo.delete(r);
    }
}
