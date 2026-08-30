package com.ecommerce.inventory_service.controller;

import com.ecommerce.inventory_service.service.InventoryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/check")
    public boolean checkStock(@RequestParam String productId, @RequestParam Integer quantity) {
        return inventoryService.isInStock(productId, quantity);
    }

    @PostMapping("/deduct")
    public String deductStock(@RequestParam String productId, @RequestParam Integer quantity) {
        inventoryService.deductStock(productId, quantity);
        return "Stock deducted successfully";
    }

    @PostMapping("/add")
    public String addStock(@RequestParam String productId, @RequestParam Integer quantity) {
        inventoryService.addStock(productId, quantity);
        return "Stock added successfully";
    }
}
