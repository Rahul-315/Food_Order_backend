package com.online.ofos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.online.ofos.entity.PartnerRestaurantRequest;
import com.online.ofos.service.PartnerRestaurantService;

@RestController
@RequestMapping("/api/admin/partner-restaurants")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPartnerRestaurantController {

    @Autowired
    private PartnerRestaurantService service;

    @GetMapping
    public ResponseEntity<List<PartnerRestaurantRequest>> getAll() {
        return ResponseEntity.ok(service.getAllRequests());
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id) {
        service.approve(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id) {
        service.reject(id);
        return ResponseEntity.ok().build();
    }
    
}
