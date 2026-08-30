package com.ecommerce.shop_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory-service", path = "/api/inventory")
public interface InventoryClient {

    @GetMapping("/check")
    boolean checkStock(@RequestParam("productId") String productId, @RequestParam("quantity") Integer quantity);

    @PostMapping("/deduct")
    String deductStock(@RequestParam("productId") String productId, @RequestParam("quantity") Integer quantity);
}
