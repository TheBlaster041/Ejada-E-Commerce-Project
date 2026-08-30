package com.ecommerce.shop_service.controller;

import com.ecommerce.shop_service.dto.OrderRequest;
import com.ecommerce.shop_service.entity.Product;
import com.ecommerce.shop_service.service.ShopService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shop")
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @PostMapping("/products")
    public Product addProduct(@RequestBody Product product) {
        return shopService.addProduct(product);
    }

    @PostMapping("/orders")
    public String placeOrder(@RequestHeader("loggedInUser") String username, @RequestBody OrderRequest request) {
        return shopService.placeOrder(username, request);
    }
}
