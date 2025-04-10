package com.tien.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload-dir}")
    private String uploadPath;
    // Cho phép truy cập ảnh
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Lấy path thư mục gốc đang chạy
//        String uploadPath = System.getProperty("user.dir") + "/uploads/";
        System.out.println(uploadPath);
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);
    }
}
