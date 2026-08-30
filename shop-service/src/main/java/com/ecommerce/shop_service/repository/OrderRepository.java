package com.ecommerce.shop_service.repository;

import com.ecommerce.shop_service.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUsername(String username);
}
