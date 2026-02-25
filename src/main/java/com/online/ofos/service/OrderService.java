package com.online.ofos.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.online.ofos.entity.Cart;
import com.online.ofos.entity.CartItem;
import com.online.ofos.entity.MenuItem;
import com.online.ofos.entity.Order;
import com.online.ofos.entity.OrderItem;
import com.online.ofos.entity.OrderStatus;
import com.online.ofos.entity.Restaurant;
import com.online.ofos.entity.User;
import com.online.ofos.exception.ResourceNotFoundException;
import com.online.ofos.repository.CartRepository;
import com.online.ofos.repository.MenuItemRepository;
import com.online.ofos.repository.OrderItemRepository;
import com.online.ofos.repository.OrderRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private OrderItemRepository orderItemRepo;

    @Autowired
    private CartRepository cartRepo;

    @Autowired
    private MenuItemRepository menuRepo;

    @Autowired
    private EmailService emailService;

    @Transactional
    public Order createOrder(Cart cart, String deliveryAddressFromRequest) {

        if (cart.getCartItems().isEmpty()) {
            throw new ResourceNotFoundException("Cart is empty");
        }

        User customer = cart.getCustomer();

        // ✅ Correct address resolution
        String deliveryAddress;

        if (deliveryAddressFromRequest != null && !deliveryAddressFromRequest.isBlank()) {
            deliveryAddress = deliveryAddressFromRequest;
        } else if (customer.getAddress() != null && !customer.getAddress().isBlank()) {
            deliveryAddress = customer.getAddress();
        } else {
            throw new ResourceNotFoundException("Delivery address is required");
        }

        double total = cart.getCartItems().stream()
                .mapToDouble(i -> i.getMenuItem().getPrice() * i.getQuantity())
                .sum();

        Order order = Order.builder()
                .customer(customer)
                .restaurant(cart.getCartItems().get(0).getMenuItem().getRestaurant())
                .orderTime(LocalDateTime.now())
                .status(OrderStatus.NEW)
                .totalAmount(total)
                .deliveryAddress(deliveryAddress)
                .build();

        order = orderRepo.save(order);

        for (CartItem cItem : cart.getCartItems()) {
            OrderItem oItem = OrderItem.builder()
                    .order(order)
                    .menuItem(cItem.getMenuItem())
                    .quantity(cItem.getQuantity())
                    .price(cItem.getMenuItem().getPrice())
                    .build();
            orderItemRepo.save(oItem);
        }

        cart.getCartItems().clear();
        cartRepo.save(cart);

        emailService.sendOrderConfirmation(
                customer.getEmail(),
                order.getId()
        );

        return order;
    }


    // ---------------- READ METHODS ----------------

    @Transactional
    public List<Order> getOrdersByCustomer(User customer) {
        return orderRepo.findByCustomer(customer);
    }

    @Transactional
    public List<Order> getOrdersByRestaurant(Restaurant restaurant) {
        return orderRepo.findByRestaurant(restaurant);
    }

    @Transactional
    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    @Transactional
    public Order getOrderById(Long id) {
        return orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    @Transactional
    public Optional<Order> findOrderById(Long id) {
        return orderRepo.findById(id);
    }

    // ---------------- UPDATE ORDER STATUS ----------------

    @Transactional
    public Order updateOrderStatus(Long id, OrderStatus status) {

        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setStatus(status);
        Order updatedOrder = orderRepo.save(order);

        emailService.sendOrderStatusUpdate(
                updatedOrder.getCustomer().getEmail(),
                updatedOrder.getId(),
                updatedOrder.getStatus().name()
        );

        return updatedOrder;
    }

    // ---------------- DELETE ORDER ----------------

 // ---------------- DELETE ORDER ----------------

    @Transactional
    public void deleteOrder(Long id) {

        Order order = getOrderById(id);

        // ✅ ALLOW delete ONLY for NEW, COMPLETED, CANCELLED
        if (
            order.getStatus() != OrderStatus.NEW &&
            order.getStatus() != OrderStatus.COMPLETED &&
            order.getStatus() != OrderStatus.CANCELLED
        ) {
            throw new ResourceNotFoundException(
                "Order cannot be deleted in status: " + order.getStatus()
            );
        }

        orderRepo.delete(order);
    }


    // ---------------- CREATE ORDER MANUALLY (ADMIN/STAFF) ----------------

    @Transactional
    public Order createOrderManually(Order order) {

        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.PENDING);
        }

        // ✅ Address safety
        if (order.getDeliveryAddress() == null || order.getDeliveryAddress().isBlank()) {
            if (order.getCustomer() != null && order.getCustomer().getAddress() != null) {
                order.setDeliveryAddress(order.getCustomer().getAddress());
            } else {
                throw new ResourceNotFoundException("Delivery address is required");
            }
        }

        double total = 0;

        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {

            List<MenuItem> menuItems = menuRepo.findByRestaurant(order.getRestaurant());
            order.setOrderItems(new ArrayList<>());
            order.setOrderTime(LocalDateTime.now());

            for (MenuItem menu : menuItems) {
                OrderItem item = OrderItem.builder()
                        .order(order)
                        .menuItem(menu)
                        .quantity(1)
                        .price(menu.getPrice())
                        .build();

                order.getOrderItems().add(item);
                orderItemRepo.save(item);
                total += menu.getPrice();
            }

        } else {

            for (OrderItem item : order.getOrderItems()) {
                MenuItem menuItem = menuRepo.findById(item.getMenuItem().getId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "MenuItem not found: " + item.getMenuItem().getId()));

                item.setMenuItem(menuItem);
                item.setOrder(order);
                orderItemRepo.save(item);
                total += item.getPrice() * item.getQuantity();
            }
        }

        order.setTotalAmount(total);
        Order savedOrder = orderRepo.save(order);

        emailService.sendOrderConfirmation(
                savedOrder.getCustomer().getEmail(),
                savedOrder.getId()
        );

        return savedOrder;
    }

    // ---------------- UPDATE ORDER ----------------

    @Transactional
    public Order updateOrder(Long id, Order updatedOrder) {

        Order existingOrder = getOrderById(id);

        if (updatedOrder.getOrderItems() != null && !updatedOrder.getOrderItems().isEmpty()) {

            if (existingOrder.getOrderItems() != null && !existingOrder.getOrderItems().isEmpty()) {
                orderItemRepo.deleteAll(existingOrder.getOrderItems());
            }

            for (OrderItem item : updatedOrder.getOrderItems()) {
                MenuItem menuItem = menuRepo.findById(item.getMenuItem().getId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "MenuItem not found: " + item.getMenuItem().getId()));

                item.setMenuItem(menuItem);
                item.setOrder(existingOrder);
                orderItemRepo.save(item);
            }

            double total = updatedOrder.getOrderItems().stream()
                    .mapToDouble(i -> i.getPrice() * i.getQuantity())
                    .sum();

            existingOrder.setTotalAmount(total);
        }

        if (updatedOrder.getStatus() != null) {
            existingOrder.setStatus(updatedOrder.getStatus());
        }
        if (updatedOrder.getCustomer() != null) {
            existingOrder.setCustomer(updatedOrder.getCustomer());
        }
        if (updatedOrder.getRestaurant() != null) {
            existingOrder.setRestaurant(updatedOrder.getRestaurant());
        }
        if (updatedOrder.getDeliveryAddress() != null) {
            existingOrder.setDeliveryAddress(updatedOrder.getDeliveryAddress());
        }

        return orderRepo.save(existingOrder);
    }
}
