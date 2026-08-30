package com.ecommerce.inventory_service.service;

import com.ecommerce.inventory_service.entity.Inventory;
import com.ecommerce.inventory_service.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public boolean isInStock(String productId, Integer quantity) {
        return inventoryRepository.findByProductId(productId)
                .map(inv -> inv.getQuantity() >= quantity)
                .orElse(false);
    }

    @Transactional
    public void deductStock(String productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Product not found in inventory: " + productId));
        if (inventory.getQuantity() < quantity) {
            throw new RuntimeException("Not enough stock for product: " + productId);
        }
        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventoryRepository.save(inventory);
    }

    @Transactional
    public void addStock(String productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId).orElseGet(() -> {
            Inventory newInv = new Inventory();
            newInv.setProductId(productId);
            newInv.setQuantity(0);
            return newInv;
        });
        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventoryRepository.save(inventory);
    }
}
