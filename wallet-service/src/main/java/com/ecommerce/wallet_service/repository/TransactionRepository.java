package com.ecommerce.wallet_service.repository;

import com.ecommerce.wallet_service.entity.Transaction;
import com.ecommerce.wallet_service.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWallet(Wallet wallet);
}
