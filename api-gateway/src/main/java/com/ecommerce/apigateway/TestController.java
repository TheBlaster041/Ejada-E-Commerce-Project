package com.ecommerce.apigateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class TestController {
    @GetMapping("/test-headers")
    public String testHeaders(@RequestHeader Map<String, String> headers) {
        return "Headers: " + headers.toString();
    }
}
