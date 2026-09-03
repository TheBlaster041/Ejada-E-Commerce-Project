package com.example.shop.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="order_items")
public class OrderItem
{
	@Id
 	@GeneratedValue(strategy=GenerationType.IDENTITY)
 	Long id;
 	String sku;
 	String name;
 	BigDecimal price;
 	private String category;
 	int quantity;
 
 	public OrderItem(){}
 	
 	public OrderItem(String sku,String name,BigDecimal price,int quantity)
 	{
	 this.sku=sku;
	 this.name=name;
	 this.price=price;
	 this.quantity=quantity;
	 }
 	
 	public Long getId()
 	{
 		return id;
 	}
 	
 	public String getSku()
 	{
 		return sku;
 	}
 	
 	public String getName()
 	{
 		return name;
 	}
 	
 	public BigDecimal getPrice()
 	{
 		return price;
 	}
 	
 	public int getQuantity()
 	{
 		return quantity;
 	}
 	public String getCategory() {
 	    return category;
 	}

 	public void setCategory(String category) {
 	    this.category = category;
 	}
}
