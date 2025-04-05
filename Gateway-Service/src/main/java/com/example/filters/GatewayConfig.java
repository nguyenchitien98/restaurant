package com.example.filters;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth", r -> r.path("/auth/**")
                        .uri("lb://auth-service"))
                .route("order", r -> r.path("/order/**")
                        .uri("lb://order-service"))
                .route("kitchen", r -> r.path("/kitchen/**")
                        .uri("lb://kitchen-service"))
                .route("report", r -> r.path("/report/**")
                        .uri("lb://report-service"))
                .build();
    }
}
