package com.ecommerce.wallet_service.controller;

import com.ecommerce.wallet_service.service.WalletService;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) 
    {
        this.walletService = walletService;
    }

    @GetMapping("/balance")
    public BigDecimal getBalance(@RequestHeader("loggedInUser") String username)
    {
        return walletService.getWalletByUsername(username).getBalance();
    }

    @PostMapping("/deposit")
    public String deposit(@RequestHeader("loggedInUser") String username,@RequestParam BigDecimal amount) 
    {
        return walletService.deposit(username, amount);
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestHeader("loggedInUser") String username,@RequestParam BigDecimal amount) 
    {
        return walletService.withdraw(username, amount);
    }
}