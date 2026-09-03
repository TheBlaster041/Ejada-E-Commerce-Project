package com.example.shop.client;

public class InventoryServiceException extends RuntimeException 
{
    private final int status;
    public InventoryServiceException(int status,String message) 
    {
        super(message);
        this.status = status;
    }
    public int getStatus() 
    {
        return status;
    }
}