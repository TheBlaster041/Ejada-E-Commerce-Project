package com.example.wallet.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name="wallet_transactions")

public class WalletTransaction
{
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Column(nullable=false)
	private String username;
	@Column(nullable=false)
	private String type;
	@Column(nullable=false,precision=19,scale=2)
	private BigDecimal amount;
	@Column(nullable=false)
	private LocalDateTime createdAt;
	
	public WalletTransaction(){}
	
	public WalletTransaction(String username,String type,BigDecimal amount)
	{
		this.username = username;
		this.type = type;
		this.amount = amount;
		this.createdAt = LocalDateTime.now();
	}
	
	public Long getId()
	{
		return id;
	}
	
	public String getUsername()
	{
		return username;
	}
	public String getType()
	{
		return type;
	}
	
	public BigDecimal getAmount()
	{
		return amount;
	}
	
	public LocalDateTime getCreatedAt()
	{
		return createdAt;
	}
}
