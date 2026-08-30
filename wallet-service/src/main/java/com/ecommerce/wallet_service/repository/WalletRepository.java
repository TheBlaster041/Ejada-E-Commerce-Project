package com.ecommerce.wallet_service.repository;

import com.ecommerce.wallet_service.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository
        extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByUsername(String username);
}