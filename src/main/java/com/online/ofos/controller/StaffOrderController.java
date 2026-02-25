package com.online.ofos.controller;

import com.online.ofos.entity.Order;
import com.online.ofos.entity.OrderStatus;
import com.online.ofos.entity.Restaurant;
import com.online.ofos.entity.User;
import com.online.ofos.repository.UserRepository;
import com.online.ofos.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff/orders")
@PreAuthorize("hasAnyRole('RESTAURANT_STAFF','ADMIN')")
public class StaffOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepo;

    @GetMapping
    public ResponseEntity<List<Order>> getOrders(Authentication auth) {

        User staff = userRepo.findByUsername(auth.getName()).get();
        Restaurant restaurant = staff.getRestaurant();

        List<Order> orders = orderService.getOrdersByRestaurant(restaurant);
        return ResponseEntity.ok(orders); 
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        Order updatedOrder = orderService.updateOrderStatus(
                id,
                Enum.valueOf(OrderStatus.class, status)
        );

        return ResponseEntity.ok(updatedOrder);
    }
}
