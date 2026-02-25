package com.online.ofos.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.online.ofos.entity.Cart;
import com.online.ofos.entity.CartItem;
import com.online.ofos.entity.MenuItem;
import com.online.ofos.entity.User;
import com.online.ofos.exception.ResourceNotFoundException;
import com.online.ofos.repository.CartItemRepository;
import com.online.ofos.repository.CartRepository;
import com.online.ofos.repository.MenuItemRepository;

import jakarta.transaction.Transactional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepo;

    @Autowired
    private CartItemRepository cartItemRepo;

    @Autowired
    private MenuItemRepository menuRepo;
    @Transactional
    public Cart getCart(User customer) {
        return cartRepo.findByCustomer(customer)
                .orElseGet(() -> {
                    Cart cart = Cart.builder()
                            .customer(customer)
                            .cartItems(new ArrayList<>())
                            .totalAmount(0)
                            .build();
                    return cartRepo.save(cart);
                });
    }
    @Transactional
    public Cart addItemToCart(User customer, Long menuItemId, int quantity) {
        MenuItem item = menuRepo.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));

        Cart cart = getCart(customer);

        Optional<CartItem> existing = cart.getCartItems().stream()
                .filter(c -> c.getMenuItem().getId().equals(menuItemId))
                .findFirst();

        if (existing.isPresent()) {
            CartItem cItem = existing.get();
            cItem.setQuantity(cItem.getQuantity() + quantity);
            cartItemRepo.save(cItem);
        } else {
            CartItem cItem = CartItem.builder()
                    .cart(cart)
                    .menuItem(item)
                    .quantity(quantity)
                    .build();
            cart.getCartItems().add(cItem);
            cartItemRepo.save(cItem);
        }

        recalculateTotal(cart);
        return cartRepo.save(cart);
    }
    @Transactional
    public void removeItem(User customer, Long cartItemId) {
        Cart cart = getCart(customer);

        CartItem item = cartItemRepo.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new ResourceNotFoundException("Item does not belong to this cart");
        }

        cart.getCartItems().remove(item);
        cartItemRepo.delete(item);

        recalculateTotal(cart);
        cartRepo.save(cart);
    }
    @Transactional
    public void clearCart(User customer) {
        Cart cart = getCart(customer);
        cart.getCartItems().clear();
        cart.setTotalAmount(0); 
        cartRepo.save(cart);
    }
    @Transactional
    private void recalculateTotal(Cart cart) {
        double total = cart.getCartItems().stream()
                .mapToDouble(c -> c.getMenuItem().getPrice() * c.getQuantity())
                .sum();
        cart.setTotalAmount(total);
    }
}
