package com.example.gateway;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication
{	
	public static void main(String[]a)
	{
		SpringApplication.run(ApiGatewayApplication.class,a);
	}
	
}
