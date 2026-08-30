package com.ecommerce.wallet_service.service;

import com.ecommerce.wallet_service.entity.Transaction;
import com.ecommerce.wallet_service.entity.Wallet;
import com.ecommerce.wallet_service.repository.TransactionRepository;
import com.ecommerce.wallet_service.repository.WalletRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public WalletService(
            WalletRepository walletRepository,
            TransactionRepository transactionRepository) {

        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    public Wallet getWalletByUsername(String username) {

        return walletRepository
                .findByUsername(username)
                .orElseGet(() -> {

                    Wallet wallet = new Wallet();

                    wallet.setUsername(username);
                    wallet.setBalance(BigDecimal.ZERO);

                    return walletRepository.save(wallet);
                });
    }

    @Transactional
    public String deposit(
            String username,
            BigDecimal amount) {

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Amount must be greater than zero"
            );
        }

        Wallet wallet = getWalletByUsername(username);

        wallet.setBalance(
                wallet.getBalance().add(amount)
        );

        Transaction transaction = new Transaction();

        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setType("DEPOSIT");

        transactionRepository.save(transaction);
        walletRepository.save(wallet);

        return "Deposit successful";
    }

    @Transactional
    public String withdraw(
            String username,
            BigDecimal amount) {

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Amount must be greater than zero"
            );
        }

        Wallet wallet = getWalletByUsername(username);

        if (wallet.getBalance().compareTo(amount) < 0) {

            throw new RuntimeException(
                    "Insufficient balance"
            );
        }

        wallet.setBalance(
                wallet.getBalance().subtract(amount)
        );

        Transaction transaction = new Transaction();

        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setType("WITHDRAWAL");

        transactionRepository.save(transaction);
        walletRepository.save(wallet);

        return "Withdrawal successful";
    }
}