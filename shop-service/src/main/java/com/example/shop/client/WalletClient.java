package com.example.shop.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Map;

@FeignClient(
        name = "WALLET-SERVICE",
        configuration = FeignAuthInterceptor.class,
        fallbackFactory = WalletClientFallbackFactory.class
)
public interface WalletClient {

    @PostMapping("/api/wallet/internal/withdraw")
    Map<String, Object> withdraw(
            @RequestParam("username") String username,
            @RequestParam("amount") BigDecimal amount
    );

    @PostMapping("/api/wallet/internal/deposit")
    Map<String, Object> deposit(
            @RequestParam("username") String username,
            @RequestParam("amount") BigDecimal amount
    );
}