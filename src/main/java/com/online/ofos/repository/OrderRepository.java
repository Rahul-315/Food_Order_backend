package com.online.ofos.repository;

import com.online.ofos.entity.Order;
import com.online.ofos.entity.Restaurant;
import com.online.ofos.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomer(User customer);
    List<Order> findByRestaurant(Restaurant restaurant);
}
