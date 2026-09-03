package com.example.inventory;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class InventoryServiceApplication
{
	public static void main(String[]a)
	{
		SpringApplication.run(InventoryServiceApplication.class,a);
	}
}
