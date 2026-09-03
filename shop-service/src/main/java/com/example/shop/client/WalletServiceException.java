package com.example.shop.client;

public class WalletServiceException extends RuntimeException 
{
    private final int status;
    public WalletServiceException(int status,String message)
    {
        super(message);
        this.status = status;
    }
    public int getStatus() 
    {
        return status;
    }
}