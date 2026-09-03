package com.example.wallet.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity
@Table(name="wallets")
public class Wallet 
{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Column(nullable=false,unique=true)
	private String username;
	@Column(nullable=false,precision=19,scale=2)
	private BigDecimal balance = BigDecimal.ZERO;
	
	public Wallet(){}
	
	public Wallet(String username)
	{
		this.username=username;
	}
	
	public Long getId()
	{
		return id;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public BigDecimal getBalance()
	{
		return balance;
	}
	
	public void setBalance(BigDecimal balance)
	{
		this.balance=balance;
	}
}
