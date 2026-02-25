package com.online.ofos.controller;

import com.online.ofos.dto.UserRequest;
import com.online.ofos.entity.User;
import com.online.ofos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserRequest req) {
        return ResponseEntity.ok(
                userService.createUser(req)
        );
    }
    @GetMapping
    public ResponseEntity<List<User>> allUsers() {
        return ResponseEntity.ok(
                userService.getAllUsers()
        );
    }
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(
                userService.getUser(id)
        );
    }
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody UserRequest req) {

        return ResponseEntity.ok(
                userService.updateUser(id, req)
        );
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build(); 
    }
}
