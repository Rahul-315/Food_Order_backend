package com.online.ofos.controller;

import com.online.ofos.dto.MenuItemRequest;
import com.online.ofos.entity.MenuItem;
import com.online.ofos.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/menu")
@PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_STAFF','CUSTOMER')") 
public class AdminMenuController {

    @Autowired
    private MenuService menuService;
    @PostMapping
    public ResponseEntity<MenuItem> addMenu(
            @RequestParam Long restaurantId,
            @RequestBody MenuItemRequest req) {

        MenuItem item = menuService.addMenuItem(restaurantId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }
    @GetMapping
    public ResponseEntity<List<MenuItem>> getMenu(@RequestParam Long restaurantId) {
        List<MenuItem> items = menuService.getMenuByRestaurant(
                menuService.getRestaurantById(restaurantId)
        );
        return ResponseEntity.ok(items);
    }
    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> updateMenu(
            @PathVariable Long id,
            @RequestBody MenuItemRequest req) {

        MenuItem item = menuService.updateMenuItem(id, req);
        return ResponseEntity.ok(item); 
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}