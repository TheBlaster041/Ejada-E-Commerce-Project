package com.ecommerce.apigateway.filter;

import com.ecommerce.apigateway.util.JwtUtil;
import io.jsonwebtoken.Claims;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter
        extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RouteValidator validator;
    private final JwtUtil jwtUtil;

    public AuthenticationFilter(
            RouteValidator validator,
            JwtUtil jwtUtil) {

        super(Config.class);

        this.validator = validator;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {

            if (!validator.isSecured.test(exchange.getRequest())) {
                return chain.filter(exchange);
            }

            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null ||
                    !authHeader.startsWith("Bearer ")) {

                return onError(
                        exchange,
                        HttpStatus.UNAUTHORIZED
                );
            }

            String token = authHeader.substring(7);

            try {

                jwtUtil.validateToken(token);

                Claims claims = jwtUtil.getClaims(token);

                String username = claims.getSubject();

                String role = claims.get(
                        "role",
                        String.class
                );

                if (role == null) {
                    role = "USER";
                }

                ServerWebExchange modifiedExchange =
                        exchange.mutate()
                                .request(
                                        exchange.getRequest()
                                                .mutate()
                                                .header(
                                                        "loggedInUser",
                                                        username
                                                )
                                                .header(
                                                        "role",
                                                        role
                                                )
                                                .build()
                                )
                                .build();

                System.out.println(
                        "Gateway authenticated user: "
                                + username
                );

                return chain.filter(modifiedExchange);

            } catch (Exception e) {

                System.out.println(
                        "JWT validation failed: "
                                + e.getMessage()
                );

                return onError(
                        exchange,
                        HttpStatus.UNAUTHORIZED
                );
            }
        };
    }

    private Mono<Void> onError(
            ServerWebExchange exchange,
            HttpStatus status) {

        exchange.getResponse().setStatusCode(status);

        return exchange.getResponse().setComplete();
    }

    public static class Config {
    }
}