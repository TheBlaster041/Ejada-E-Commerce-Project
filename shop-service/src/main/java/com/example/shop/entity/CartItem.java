package com.example.shop.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="cart_items")
public class CartItem {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long id;
    String sku;
    String name;
    BigDecimal price;
    String imagePath;
    private String category;
    int quantity;

    public CartItem() {}
    

    public CartItem(String sku, String name, BigDecimal price, String imagePath, int quantity) 
    {
        this.sku=sku;
        this.name=name;
        this.price=price;
        this.imagePath=imagePath;
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
    
    public String getImagePath()
    {
    	return imagePath;
    }
    
    public int getQuantity()
    {
    	return quantity;
    }
    
    public void setQuantity(int quantity)
    {
    	this.quantity=quantity;
    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
