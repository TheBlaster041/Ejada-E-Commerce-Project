package com.example.shop.client;

import feign.FeignException;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class WalletClientFallbackFactory
        implements FallbackFactory<WalletClient> {

    @Override
    public WalletClient create(Throwable cause) {

        logCause(cause);

        return new WalletClient() {

            @Override
            public Map<String, Object> withdraw(
                    String username,
                    BigDecimal amount) {

                throw createException(cause);
            }

            @Override
            public Map<String, Object> deposit(
                    String username,
                    BigDecimal amount) {

                throw createException(cause);
            }
        };
    }

    private RuntimeException createException(
            Throwable cause) {

        FeignException feignException =
                findFeignException(cause);

        if (feignException != null) {

            int status = feignException.status();

            String message =
                    extractMessage(
                            feignException.contentUTF8()
                    );

            return new WalletServiceException(
                    status,
                    message
            );
        }

        return new WalletServiceException(
                503,
                "Wallet service unavailable"
        );
    }

    private FeignException findFeignException(
            Throwable cause) {

        Throwable current = cause;

        while (current != null) {

            if (current instanceof FeignException) {
                return (FeignException) current;
            }

            current = current.getCause();
        }

        return null;
    }

    private String extractMessage(String body) {

        if (body == null || body.isBlank()) {
            return "Wallet request failed";
        }

        try {

            int start =
                    body.indexOf("\"message\"");

            if (start >= 0) {

                start =
                        body.indexOf(":", start) + 1;

                start =
                        body.indexOf("\"", start) + 1;

                int end =
                        body.indexOf("\"", start);

                if (end > start) {
                    return body.substring(
                            start,
                            end
                    );
                }
            }

        } catch (Exception ignored) {
        }

        return body;
    }

    private void logCause(Throwable cause) {

        System.out.println(
                "================================="
        );

        System.out.println(
                "WALLET FEIGN ERROR"
        );

        System.out.println(
                "TYPE: "
                + cause.getClass().getName()
        );

        System.out.println(
                "MESSAGE: "
                + cause.getMessage()
        );

        Throwable current =
                cause.getCause();

        while (current != null) {

            System.out.println(
                    "CAUSE: "
                    + current.getClass().getName()
                    + " -> "
                    + current.getMessage()
            );

            current =
                    current.getCause();
        }

        System.out.println(
                "================================="
        );
    }
}