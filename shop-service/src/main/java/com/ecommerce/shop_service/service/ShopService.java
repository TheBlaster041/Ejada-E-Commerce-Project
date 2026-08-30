package com.ecommerce.shop_service.service;

import com.ecommerce.shop_service.client.InventoryClient;
import com.ecommerce.shop_service.client.WalletClient;
import com.ecommerce.shop_service.dto.OrderItemRequest;
import com.ecommerce.shop_service.dto.OrderRequest;
import com.ecommerce.shop_service.entity.Order;
import com.ecommerce.shop_service.entity.OrderItem;
import com.ecommerce.shop_service.entity.Product;
import com.ecommerce.shop_service.repository.OrderRepository;
import com.ecommerce.shop_service.repository.ProductRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShopService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final WalletClient walletClient;

    public ShopService(ProductRepository productRepository, OrderRepository orderRepository, InventoryClient inventoryClient, WalletClient walletClient) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.inventoryClient = inventoryClient;
        this.walletClient = walletClient;
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    @CircuitBreaker(name = "walletService", fallbackMethod = "placeOrderFallback")
    public String placeOrder(String username, OrderRequest request) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            
            // Check stock using Feign Client
            boolean inStock = inventoryClient.checkStock(product.getId().toString(), itemReq.getQuantity());
            if (!inStock) {
                throw new RuntimeException("Product " + product.getName() + " is out of stock or insufficient quantity");
            }
            
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
            
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(itemReq.getQuantity());
            orderItems.add(orderItem);
        }

        // Deduct money using Wallet Feign Client
        walletClient.withdraw(username, totalAmount);
        
        // Deduct stock
        for (OrderItemRequest itemReq : request.getItems()) {
            inventoryClient.deductStock(itemReq.getProductId().toString(), itemReq.getQuantity());
        }

        Order order = new Order();
        order.setUsername(username);
        order.setTotalAmount(totalAmount);
        order.setStatus("COMPLETED");
        order.setItems(orderItems);
        orderRepository.save(order);

        return "Order placed successfully. Total amount: " + totalAmount;
    }

    public String placeOrderFallback(String username, OrderRequest request, Throwable t) {
        return "Order failed. Service is currently unavailable. Please try again later. Error: " + t.getMessage();
    }
}
