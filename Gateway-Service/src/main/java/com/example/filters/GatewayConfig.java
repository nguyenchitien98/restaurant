package com.example.filters;

import org.springframework.cloud.gateway.filter.factory.DedupeResponseHeaderGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//cấu hình các route thủ công qua Java DSL
@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
//                .route("user", r -> r.path("/api/users/**")
//                        .uri("lb://User-Service"))
//                .route("order", r -> r.path("/api/orders/**")
//                        .uri("lb://Order-Service"))
//                .route("menu", r -> r.path("/api/menus/**")
//                        .uri("lb://Menu-Service"))
//                .route("kitchen", r -> r.path("/api/kitchens/**")
//                        .uri("lb://Kitchen-Service"))
//                .route("invoice", r -> r.path("/api/invoice/**")
//                        .uri("lb://Invoice-Service"))
//                .route("report", r -> r.path("/api/reports/**")
//                        .uri("lb://Report-Service"))
                .route("user", r -> r.path("/api/users/**","/api/auth/**")
                        .uri("http://localhost:8081"))
                .route("order", r -> r.path("/api/orders/**","/api/tables/**","/api/order-details/**")
                        .uri("http://localhost:8083"))
                .route("menu", r -> r.path("/api/menus/**","/uploads/**")
                        .uri("http://localhost:8082"))
                .route("kitchen", r -> r.path("/api/kitchens/**")
                        .uri("http://localhost:8084"))
                .route("invoice", r -> r.path("/api/invoice/**")
                        .uri("http://localhost:8085"))
                .route("report", r -> r.path("/api/reports/**","/ws-report/**").filters(f -> f.dedupeResponseHeader("Access-Control-Allow-Origin", DedupeResponseHeaderGatewayFilterFactory.Strategy.RETAIN_FIRST.name()))
                        .uri("http://localhost:8086"))
                .build();
    }
}
