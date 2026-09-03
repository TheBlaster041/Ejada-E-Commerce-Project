package com.example.inventory.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;

    public Category() 
    {
    }

    public Category(String name) 
    {
        this.name = name;
    }

    public Long getCategoryId() 
    {
        return id;
    }

    public void setCategoryId(Long id) 
    {
        this.id = id;
    }

    public String getName() 
    {
        return name;
    }

    public void setName(String name) 
    {
        this.name = name;
    }
}