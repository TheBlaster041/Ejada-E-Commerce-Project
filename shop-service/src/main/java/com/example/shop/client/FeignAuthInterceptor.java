package com.example.shop.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignAuthInterceptor {

    @Bean
    public RequestInterceptor requestInterceptor() {

        return new RequestInterceptor() {

            @Override
            public void apply(RequestTemplate template) {

                ServletRequestAttributes attributes =
                        (ServletRequestAttributes)
                                RequestContextHolder.getRequestAttributes();

                if (attributes == null) {
                    System.out.println("FEIGN: No request attributes");
                    return;
                }

                HttpServletRequest request =
                        attributes.getRequest();

                String authorization =
                        request.getHeader("Authorization");

                System.out.println(
                        "FEIGN URL: " + template.url()
                );

                System.out.println(
                        "FEIGN AUTH PRESENT: "
                        + (authorization != null
                        && !authorization.isBlank())
                );

                if (authorization != null
                        && !authorization.isBlank()) {

                    template.header(
                            "Authorization",
                            authorization
                    );

                    System.out.println(
                            "FEIGN: Authorization header forwarded"
                    );
                }
            }
        };
    }
}