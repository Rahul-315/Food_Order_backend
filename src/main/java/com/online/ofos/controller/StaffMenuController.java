package com.online.ofos.controller;

import com.online.ofos.dto.MenuItemRequest;
import com.online.ofos.entity.MenuItem;
import com.online.ofos.entity.User;
import com.online.ofos.repository.UserRepository;
import com.online.ofos.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff/menu")
@PreAuthorize("hasAnyRole('RESTAURANT_STAFF','ADMIN','CUSTOMER')")
public class StaffMenuController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private UserRepository userRepo;

    @PostMapping
    public ResponseEntity<MenuItem> addMenu(
            @RequestBody MenuItemRequest req,
            Authentication auth) {

        User staff = userRepo.findByUsername(auth.getName()).get();
        MenuItem item = menuService.addMenuItem(staff.getRestaurant().getId(), req);
        return ResponseEntity.status(HttpStatus.CREATED).body(item); 
    }

    @GetMapping
    public ResponseEntity<List<MenuItem>> getMenu(Authentication auth) {
        User staff = userRepo.findByUsername(auth.getName()).get();
        List<MenuItem> items = menuService.getMenuByRestaurant(staff.getRestaurant());
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