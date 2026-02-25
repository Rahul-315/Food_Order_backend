package com.online.ofos.controller;

import com.online.ofos.entity.Cart;
import com.online.ofos.entity.Order;
import com.online.ofos.entity.OrderStatus;
import com.online.ofos.entity.Restaurant;
import com.online.ofos.entity.User;
import com.online.ofos.repository.UserRepository;
import com.online.ofos.service.CartService;
import com.online.ofos.service.OrderService;
import com.online.ofos.service.RestaurantService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerOrderController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RestaurantService restaurantService;

    // -------- CART --------

    @GetMapping("/cart")
    public ResponseEntity<Cart> getCart(Authentication auth) {
        User customer = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(cartService.getCart(customer));
    }

    @PostMapping("/cart")
    public ResponseEntity<Cart> addToCart(
            Authentication auth,
            @RequestParam Long menuItemId,
            @RequestParam int quantity) {

        User customer = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cartService.addItemToCart(customer, menuItemId, quantity));
    }

    @DeleteMapping("/cart/{cartItemId}")
    public ResponseEntity<Void> removeFromCart(
            Authentication auth,
            @PathVariable Long cartItemId) {

        User customer = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        cartService.removeItem(customer, cartItemId);
        return ResponseEntity.noContent().build();
    }

    // -------- ORDER --------

    /**
     * If deliveryAddress is not sent,
     * registered user address will be used automatically
     */
    @PostMapping("/order")
    public ResponseEntity<Order> placeOrder(
            Authentication auth,
            @RequestParam(required = false) String deliveryAddress) {

        User customer = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartService.getCart(customer);

        Order order = orderService.createOrder(cart, deliveryAddress);

        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> myOrders(Authentication auth) {
        User customer = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(orderService.getOrdersByCustomer(customer));
    }

    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<Void> deleteOrder(
            Authentication auth,
            @PathVariable Long orderId) {

        User customer = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderService.findOrderById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // ❌ Not customer's order
        if (!order.getCustomer().getId().equals(customer.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // ❌ Block delete after staff accepts
        if ( order.getStatus() != OrderStatus.NEW &&
        	    order.getStatus() != OrderStatus.CANCELLED &&
        	    order.getStatus() != OrderStatus.COMPLETED) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }


    // -------- RESTAURANTS --------

    @GetMapping("/restaurants")
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    @GetMapping("/restaurants/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getRestaurant(id));
    }
}
