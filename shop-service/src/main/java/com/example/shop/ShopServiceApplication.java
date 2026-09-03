package com.example.shop;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ShopServiceApplication
{
	public static void main(String[]a)
	{
		SpringApplication.run(ShopServiceApplication.class,a);
		}
}
