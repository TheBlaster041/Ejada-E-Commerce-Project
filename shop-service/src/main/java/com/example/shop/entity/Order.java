package com.example.shop.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name="orders")
public class Order
{
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	Long id;
	String username;
	@Column(nullable=false,precision=19,scale=2)
	BigDecimal total;
	String status;
	LocalDateTime createdAt;
	@OneToMany(cascade=CascadeType.ALL)
	List<OrderItem> items=new ArrayList<>();
	
	public Order(){}
	
	public Long getId()
	{
		return id;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public void setUsername(String username)
	{
		this.username=username;
	}
	
	public BigDecimal getTotal()
	{
		return total;
	}
	
	public void setTotal(BigDecimal total)
	{
		this.total=total;
	}
	
	public String getStatus()
	{
		return status;
	}
	
	public void setStatus(String status)
	{
		this.status=status;
	}
	
	public LocalDateTime getCreatedAt()
	{
		return createdAt;
	}
	
	public void setCreatedAt(LocalDateTime createdAt)
	{
		this.createdAt=createdAt;
	}
	
	public List<OrderItem> getItems()
	{
		return items;
	}
}
