package com.example.shop.controller;

import com.example.shop.entity.*;
import com.example.shop.repository.*;
import com.example.shop.client.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/shop")
public class ShopController {

    private final CartRepository carts;
    private final OrderRepository orders;
    private final InventoryClient inventory;
    private final WalletClient wallet;

    public ShopController(CartRepository carts,OrderRepository orders,InventoryClient inventory,WalletClient wallet)
    {
        this.carts = carts;
        this.orders = orders;
        this.inventory = inventory;
        this.wallet = wallet;
    }

    @GetMapping("/cart")
    public Cart cart(java.security.Principal p) 
    {
        return carts.findByUsername(p.getName()).orElseGet(() ->carts.save(new Cart(p.getName())));
    }

    @PostMapping("/cart/items")
    public ResponseEntity<?> addItem(java.security.Principal p, @RequestBody Map<String, Object> body) 
    {
        String sku = (String) body.get("sku");
        Object quantityValue = body.get("quantity");
        if (sku == null || sku.isBlank()) 
            return ResponseEntity.badRequest().body(Map.of("message","sku is required"));
        
        if (!(quantityValue instanceof Number)) 
            return ResponseEntity.badRequest().body(Map.of("message","quantity must be a number"));

        int qty =((Number) quantityValue).intValue();
        if (qty <= 0) 
            return ResponseEntity.badRequest().body(Map.of("message","quantity must be positive"));
        
        Map<String, Object> product;
        try 
        {
            product = inventory.get(sku);
        } 
        catch (InventoryServiceException e) 
        {
            return ResponseEntity.status(e.getStatus()).body(Map.of("message",e.getMessage()));
        }

        if (product == null || product.get("id") == null)
            return ResponseEntity.status(404).body(Map.of("message","product not found"));

        int stock =((Number) product.get("quantity")).intValue();
        if (stock < qty)
            return ResponseEntity.badRequest().body(Map.of("message","insufficient stock"));
        
        Cart cart = carts.findByUsername(p.getName()).orElseGet(() ->new Cart(p.getName()));
        Optional<CartItem> existing = cart.getItems().stream().filter(item ->item.getSku().equals(sku)).findFirst();
        if (existing.isPresent())
            existing.get().setQuantity( existing.get().getQuantity()+ qty);

        else 
        {
        	CartItem cartItem = new CartItem(sku,(String) product.get("name"),new BigDecimal(product.get("price").toString()),(String) product.get("imagePath"),qty);
            Map<String, Object> category = (Map<String, Object>) product.get("category");
            if (category != null) 
                cartItem.setCategory((String) category.get("name"));

            cart.getItems().add(cartItem);
        }
        return ResponseEntity.ok(carts.save(cart));
    }


    @DeleteMapping("/cart/items/{sku}")
    public ResponseEntity<?> remove(@PathVariable String sku,java.security.Principal p) 
    {
        return carts.findByUsername(p.getName()).map(c -> {c.getItems().removeIf(item ->item.getSku().equals(sku));
                    carts.save(c);
                    return ResponseEntity.ok(c);
                }).orElseGet(() -> ResponseEntity.notFound().build()
                );
    }


    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(java.security.Principal p) 
    {
        String username = p.getName();


        Cart cart = carts.findByUsername(username).orElse(null);

        if (cart == null || cart.getItems().isEmpty())
            return ResponseEntity.badRequest().body(Map.of("message","cart is empty"));

        BigDecimal total =cart.getItems().stream().map(item ->item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))).reduce(BigDecimal.ZERO,BigDecimal::add);

        try {
            for (CartItem item : cart.getItems()) 
            {
                Map<String, Object> product = inventory.get(item.getSku());
                if (product == null || product.get("id") == null)
                    return ResponseEntity.status(404).body(Map.of("message","product not found: "+ item.getSku()));
                
                int stock = ((Number) product.get("quantity")).intValue();
                if (stock <item.getQuantity())
                
                    return ResponseEntity.badRequest().body(Map.of("message","insufficient stock for product: "+ item.getSku(),"available",stock,"requested",item.getQuantity()));
            }
        }
        catch (InventoryServiceException e) 
        {
            return ResponseEntity.status(e.getStatus()).body(Map.of("message",e.getMessage()));
        }

        try 
        {
            wallet.withdraw(username,total);
        } 
        catch (WalletServiceException e) 
        {
            return ResponseEntity.status(e.getStatus()).body(Map.of("message",e.getMessage()));
        }

        List<CartItem> removedItems = new java.util.ArrayList<>();
        try {
            for (CartItem item : cart.getItems()) 
            {
                inventory.remove(item.getSku(),item.getQuantity());
                removedItems.add(item);
            }
        } 
        catch (InventoryServiceException e) 
        {
            try 
            {
                wallet.deposit(username,total);
                System.out.println("PAYMENT REFUNDED: "+ total);
            } 
            catch (WalletServiceException refundError) 
            {
            	System.out.println("CRITICAL: PAYMENT REFUND FAILED");
                System.out.println("REFUND ERROR: "+ refundError.getMessage());
                return ResponseEntity.status(500).body(Map.of("message","Inventory failed and payment refund failed","inventoryError",e.getMessage(),"refundError",refundError.getMessage()));
            }
            
            return ResponseEntity.status(e.getStatus()).body(Map.of("message",e.getMessage(),"payment","refunded"));
        }

        Order order =new Order();
        order.setUsername(username);
        order.setTotal(total);
        order.setStatus("PAID");
        order.setCreatedAt(LocalDateTime.now());
        
        for (CartItem item : cart.getItems()) 
        {
        	OrderItem orderItem = new OrderItem(item.getSku(),item.getName(),item.getPrice(),item.getQuantity());
            orderItem.setCategory(item.getCategory());

            order.getItems().add(orderItem);
        }
        orders.save(order);
        cart.getItems().clear();
        carts.save(cart);

        return ResponseEntity.status(201).body(order);
    }

    @GetMapping("/orders")
    public List<Order> orders(java.security.Principal p) 
    {
        return orders.findByUsernameOrderByCreatedAtDesc(p.getName());
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<?> order(@PathVariable Long id, java.security.Principal p) 
    {
        return orders.findById(id).filter(order ->order.getUsername().equals(p.getName())).map(ResponseEntity::ok).orElseGet(() ->ResponseEntity.notFound().build());
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<?> status(@PathVariable Long id,@RequestParam String status)
    {
        return orders.findById(id).map(order -> { order.setStatus(status.toUpperCase());
        return ResponseEntity.ok(orders.save(order));}).orElseGet(() ->ResponseEntity.notFound().build());
    }
}