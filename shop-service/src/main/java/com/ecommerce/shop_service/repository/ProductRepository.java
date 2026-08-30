package com.ecommerce.shop_service.repository;

import com.ecommerce.shop_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
