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
                .route("user", r -> r.path("/api/users/**")
                        .uri("lb://User-Service"))
                .route("order", r -> r.path("/api/orders/**")
                        .uri("lb://Order-Service"))
                .route("menu", r -> r.path("/api/menus/**")
                        .uri("lb://Menu-Service"))
                .route("kitchen", r -> r.path("/api/kitchens/**")
                        .uri("lb://Kitchen-Service"))
                .route("invoice", r -> r.path("/api/invoice/**")
                        .uri("lb://Invoice-Service"))
                .route("report", r -> r.path("/api/reports/**")
                        .uri("lb://Report-Service"))
                .build();
    }
}
