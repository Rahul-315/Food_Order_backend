package com.online.ofos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.online.ofos.entity.PartnerRestaurantRequest;

public interface PartnerRestaurantRequestRepository
        extends JpaRepository<PartnerRestaurantRequest, Long> {
}
