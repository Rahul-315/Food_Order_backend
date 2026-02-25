package com.online.ofos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.online.ofos.dto.PartnerRestaurantRequestDTO;
import com.online.ofos.entity.PartnerRestaurantRequest;
import com.online.ofos.entity.Restaurant;
import com.online.ofos.repository.PartnerRestaurantRequestRepository;
import com.online.ofos.repository.RestaurantRepository;

import jakarta.transaction.Transactional;

@Service
public class PartnerRestaurantService {

    @Autowired
    private PartnerRestaurantRequestRepository partnerRepo;

    @Autowired
    private RestaurantRepository restaurantRepo;

    // ==========================
    // CUSTOMER → SUBMIT REQUEST
    // ==========================
    @Transactional
    public PartnerRestaurantRequest submitRequest(PartnerRestaurantRequestDTO dto) {

        PartnerRestaurantRequest req = PartnerRestaurantRequest.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .description(dto.getDescription())
                .contactEmail(dto.getContactEmail())
                .contactPhone(dto.getContactPhone())
                .imageUrl(dto.getImageUrl())
                .status(PartnerRestaurantRequest.Status.PENDING)
               
                .build();

        return partnerRepo.save(req);
    }

    // ==========================
    // ADMIN → VIEW ALL REQUESTS
    // ==========================
    public List<PartnerRestaurantRequest> getAllRequests() {
        return partnerRepo.findAll();
    }

    // ==========================
    // ADMIN → APPROVE
    // ==========================
    @Transactional
    public void approve(Long id) {

        PartnerRestaurantRequest req =
                partnerRepo.findById(id)
                        .orElseThrow(() -> new RuntimeException("Request not found"));

        // Create Restaurant safely
        Restaurant restaurant = Restaurant.builder()
                .name(req.getName())
                .address(req.getAddress())
                .description(req.getDescription())
                .contactEmail(req.getContactEmail())
                .contactPhone(req.getContactPhone())
                .imageUrl(req.getImageUrl())
                  // ✅ IMPORTANT
                .build();

        restaurantRepo.save(restaurant);

        // Update request status
        req.setStatus(PartnerRestaurantRequest.Status.APPROVED);
        partnerRepo.save(req);
        deleteAfterDelay(req, 1_000);
    }

    // ==========================
    // ADMIN → REJECT
    // ==========================
    @Transactional
    public void reject(Long id) {

        PartnerRestaurantRequest req =
                partnerRepo.findById(id)
                        .orElseThrow(() -> new RuntimeException("Request not found"));

        req.setStatus(PartnerRestaurantRequest.Status.REJECTED);
        partnerRepo.save(req);
       deleteAfterDelay(req, 1_000);
    }
    @Transactional
    public void delete(Long id) {
        PartnerRestaurantRequest req =
                partnerRepo.findById(id)
                        .orElseThrow(() -> new RuntimeException("Request not found"));

        partnerRepo.delete(req);
    }
    @Async
    public void deleteAfterDelay(PartnerRestaurantRequest req, long delayMillis) {
        try {
            Thread.sleep(delayMillis);  // wait
            partnerRepo.deleteById(req.getId());
            System.out.println("Deleted request with id: " + req.getId());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
