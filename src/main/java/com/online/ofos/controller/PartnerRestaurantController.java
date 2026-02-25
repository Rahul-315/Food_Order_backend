package com.online.ofos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.online.ofos.dto.PartnerRestaurantRequestDTO;
import com.online.ofos.service.PartnerRestaurantService;

import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/partner")
public class PartnerRestaurantController {

    @Autowired
    private PartnerRestaurantService service;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody  PartnerRestaurantRequestDTO dto) {

        return ResponseEntity.ok(service.submitRequest(dto));
    }
}
