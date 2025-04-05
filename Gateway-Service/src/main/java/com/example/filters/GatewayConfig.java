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
                .route("auth", r -> r.path("/api/auth/**")
                        .uri("lb://auth-service"))
                .route("order", r -> r.path("/api/orders/**")
                        .uri("lb://Order-Service"))
                .route("kitchen", r -> r.path("/api/kitchen/**")
                        .uri("lb://kitchen-service"))
                .route("report", r -> r.path("/api/report/**")
                        .uri("lb://report-service"))
                .build();
    }
}
