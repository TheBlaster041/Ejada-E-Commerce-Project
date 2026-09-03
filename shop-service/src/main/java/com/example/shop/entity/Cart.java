package com.example.shop.entity;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name="carts")

public class Cart{
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY) 
	Long id;
	@Column(nullable=false,unique=true)
	String username;
	@OneToMany(cascade=CascadeType.ALL,orphanRemoval=true)
	List<CartItem> items=new ArrayList<>();
 
	public Cart(){}
	
	public Cart(String username)
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
	
	public List<CartItem> getItems()
	{
		return items;
	}
}
