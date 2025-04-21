package com.tien.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Restaurant API",
                version = "1.0",
                description = "API for managing orders, menus, tables..."
        )
)
public class OpenApiConfig {
    // Có thể thêm cấu hình mở rộng tại đây nếu cần
}
