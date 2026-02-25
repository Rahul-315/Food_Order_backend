package com.online.ofos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.online.ofos.dto.UserRequest;
import com.online.ofos.entity.Restaurant;
import com.online.ofos.entity.Role;
import com.online.ofos.entity.User;
import com.online.ofos.exception.ResourceNotFoundException;
import com.online.ofos.repository.RestaurantRepository;
import com.online.ofos.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RestaurantRepository restaurantRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

   
    @Autowired
    private EmailService emailService;

    @Transactional
    public User createUser(UserRequest req) {

        Restaurant restaurant = null;

        if (req.getRole() == Role.RESTAURANT_STAFF) {
            restaurant = restaurantRepo.findById(req.getRestaurantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        }

        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(req.getRole())
                .mobileNumber(req.getMobileNumber())
                .restaurant(restaurant)
                .address(req.getAddress())
                .build();

     
        User savedUser = userRepo.save(user);

        
        if (savedUser.getEmail() != null && !savedUser.getEmail().isBlank()) {
            emailService.sendWelcomeMail(
                    savedUser.getEmail(),
                    savedUser.getUsername()
            );
        }

        return savedUser;
    }

    @Transactional
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Transactional
    public User getUser(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    public User updateUser(Long id, UserRequest req) {

        User user = getUser(id);

        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setRole(req.getRole());
        user.setAddress(req.getAddress());
        user.setMobileNumber(req.getMobileNumber());

        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }

        if (req.getRole() == Role.RESTAURANT_STAFF) {

            if (req.getRestaurantId() == null) {
                throw new IllegalArgumentException("Restaurant ID is required for staff");
            }

            Restaurant restaurant = restaurantRepo.findById(req.getRestaurantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

            user.setRestaurant(restaurant);

        } else {
            user.setRestaurant(null);
        }

        return userRepo.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepo.delete(getUser(id));
    }
}
