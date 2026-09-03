package com.example.inventory.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="products")
public class Product {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String sku;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false, precision=19, scale=2)
    private BigDecimal price;

    @Column(nullable=false)
    private Integer quantity = 0;
    
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Column(length=500)
    private String imagePath;

    public Product() {}

    public Long getId()
    {
    	return id;
    }
    
    public String getSku()
    {
    	return sku;
    }
    
    public void setSku(String sku)
    { 
    	this.sku=sku;
    }
    
    public String getName()
    {
    	return name;
    }
    
    public void setName(String name)
    {
    	this.name=name;
    }
    
    public BigDecimal getPrice()
    {
    	return price;
    }
    
    public void setPrice(BigDecimal price)
    {
    	this.price=price;
    }
    
    public Integer getQuantity()
    {
    	return quantity;
    }
    
    public void setQuantity(Integer quantity)
    {
    	this.quantity=quantity;
    }
    
    public String getImagePath()
    {
    	return imagePath;
    }
    
    public void setImagePath(String imagePath)
    {
    	this.imagePath=imagePath;
    }
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
    
}
