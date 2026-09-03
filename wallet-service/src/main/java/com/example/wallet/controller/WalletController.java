package com.example.wallet.controller;
import com.example.wallet.entity.*;
import com.example.wallet.repository.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.*;

@RestController @RequestMapping("/api/wallet")
public class WalletController 
{
	private final WalletRepository wallets;
	private final TransactionRepository transactions;
	public WalletController(WalletRepository wallets,TransactionRepository transactions)
	{
		this.wallets = wallets;
		this.transactions = transactions;
	}

	@PostMapping("/create")
	public ResponseEntity<?> create(java.security.Principal p)
	{
		if(wallets.findByUsername(p.getName()).isPresent())
			return ResponseEntity.status(409).body(Map.of("message","wallet already exists"));
		
		return ResponseEntity.status(201).body(wallets.save(new Wallet(p.getName())));
	}
	
	@GetMapping("/balance") public ResponseEntity<?> balance(java.security.Principal p)
	{
		return wallets.findByUsername(p.getName()).map(w->ResponseEntity.ok(Map.of("username",p.getName(),"balance",w.getBalance())))
									 .orElseGet(()->ResponseEntity.status(404).body(Map.of("message","wallet not found")));
	}
	
	@PostMapping("/deposit") public ResponseEntity<?> deposit(@RequestParam BigDecimal amount,java.security.Principal p)
	{
		if(amount.signum()<=0)
			return ResponseEntity.badRequest().body(Map.of("message","amount must be positive"));
		
		Wallet w = wallets.findByUsername(p.getName()).orElseGet(()->wallets.save(new Wallet(p.getName())));
		w.setBalance(w.getBalance().add(amount)); wallets.save(w); transactions.save(new WalletTransaction(p.getName(),"DEPOSIT",amount));
		
		return ResponseEntity.ok(Map.of("balance",w.getBalance(),"message","deposit successful"));
	}
	
	@PostMapping("/withdraw") public ResponseEntity<?> withdraw(@RequestParam BigDecimal amount,java.security.Principal p)
	{
		if(amount.signum()<=0)
			return ResponseEntity.badRequest().body(Map.of("message","amount must be positive"));
		
		Wallet w = wallets.findByUsername(p.getName()).orElse(null);
		if(w==null)
			return ResponseEntity.status(404).body(Map.of("message","wallet not found"));
		
		if(w.getBalance().compareTo(amount)<0)
			return ResponseEntity.badRequest().body(Map.of("message","insufficient balance"));
		
		w.setBalance(w.getBalance().subtract(amount)); wallets.save(w); transactions.save(new WalletTransaction(p.getName(),"WITHDRAW",amount));
		return ResponseEntity.ok(Map.of("balance",w.getBalance(),"message","withdrawal successful"));
	}
	
	@GetMapping("/transactions")
	public List<WalletTransaction> transactions(java.security.Principal p)
	{
		return transactions.findByUsernameOrderByCreatedAtDesc(p.getName());
	}
	
	@PostMapping("/internal/withdraw")
	public ResponseEntity<?> internalWithdraw(@RequestParam String username,@RequestParam BigDecimal amount) 
	{
	    if (amount == null || amount.signum() <= 0) 
	    	return ResponseEntity.badRequest().body(Map.of("message","amount must be positive"));
	    Wallet w = wallets.findByUsername(username).orElse(null);
	    if (w == null) 
	    	return ResponseEntity.status(404).body(Map.of("message","wallet not found"));
	    
	    if (w.getBalance().compareTo(amount) < 0) 
	        return ResponseEntity.badRequest().body(Map.of("message","insufficient balance"));
	    
	    w.setBalance(w.getBalance().subtract(amount));
	    wallets.save(w);
	    transactions.save(new WalletTransaction(username,"PAYMENT",amount));

	    return ResponseEntity.ok(Map.of("success", true,"balance", w.getBalance(),"message", "payment successful"));
	}
	
	@PostMapping("/internal/deposit")
	public ResponseEntity<?> internalDeposit(@RequestParam String username,@RequestParam BigDecimal amount) 
	{
	    if (amount == null || amount.signum() <= 0) 
	        return ResponseEntity.badRequest().body(Map.of("message","amount must be positive"));
	    
	    Wallet w = wallets.findByUsername(username).orElseGet(() ->wallets.save(new Wallet(username)));
	    w.setBalance(w.getBalance().add(amount));
	    wallets.save(w);
	    transactions.save(new WalletTransaction(username,"REFUND",amount));
	    
	    return ResponseEntity.ok(Map.of("success", true,"balance", w.getBalance(),"message", "refund successful"));
	}
}
