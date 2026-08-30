package com.ecommerce.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class TestFilter implements org.springframework.cloud.gateway.filter.GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("Global filter executed for path: " + exchange.getRequest().getURI().getPath());
        return chain.filter(exchange);
    }
}
