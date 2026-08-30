package com.ecommerce.shop_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;

@FeignClient(name = "wallet-service", path = "/api/wallet")
public interface WalletClient {

    @PostMapping("/withdraw")
    String withdraw(@RequestHeader("loggedInUser") String username, @RequestParam("amount") BigDecimal amount);
}
