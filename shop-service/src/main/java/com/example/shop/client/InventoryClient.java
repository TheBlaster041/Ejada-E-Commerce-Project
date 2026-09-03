package com.example.shop.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(
        name = "INVENTORY-SERVICE",
        configuration = FeignAuthInterceptor.class,
        fallbackFactory = InventoryClientFallbackFactory.class
)
public interface InventoryClient 
{
    @GetMapping("/api/inventory/products/{sku}")
    Map<String, Object> get(@PathVariable("sku") String sku);

    @PostMapping("/api/inventory/stock/{sku}/remove")
    Map<String, Object> remove(@PathVariable("sku") String sku,@RequestParam("quantity") int quantity);
}